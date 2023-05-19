package com.v7878.unsafe.function;

import com.v7878.unsafe.memory.Addressable;
import com.v7878.unsafe.memory.Pointer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO make non-public
public class MMap {

    public static final int PERMISSION_READ = 0b1;
    public static final int PERMISSION_WRITE = 0b10;
    public static final int PERMISSION_EXECUTE = 0b100;
    public static final int PERMISSION_SHARED = 0b1000;
    public static final int PERMISSION_PRIVATE = 0b10000;
    public static final int PERMISSION_GENERATED = 1 << 31;

    public static class MMapEntry implements Comparable<MMapEntry>, Addressable {

        private final long offset, size;
        private final int perms;
        private final Pointer pointer;

        public MMapEntry(long offset, long size, int perms, Addressable address) {
            this.offset = offset;
            this.size = size;
            this.perms = perms;
            this.pointer = address.pointer();
        }

        public MMapEntry(long offset, long size, int perms, long address) {
            this(offset, size, perms, new Pointer(address));
        }

        @Override
        public Pointer pointer() {
            return pointer;
        }

        public long size() {
            return size;
        }

        public int perms() {
            return perms;
        }

        public long file_start() {
            return offset;
        }

        public long file_end() {
            return file_start() + size;
        }

        public long start() {
            return pointer.getRawAddress();
        }

        public long end() {
            return start() + size;
        }

        @Override
        public String toString() {
            return "{" + offset + " " + size + " "
                    + pointer + " " + Integer.toBinaryString(perms) + "}";
        }

        @Override
        public int compareTo(MMapEntry other) {
            return Long.compare(offset, other.offset);
        }
    }

    public static class MMapFile implements Comparable<String> {

        public static interface Filter {

            public static final Filter DEFAULT = unused -> true;
            public static final Filter GENERATED = perm(PERMISSION_GENERATED);

            public static Filter perm(int perms) {
                return entry -> (entry.perms == perms);
            }

            public boolean filter(MMapEntry entry);
        }

        public final String path;
        public final List<MMapEntry> entries = new ArrayList<>();

        public MMapFile(String path, MMapEntry first) {
            this.path = path;
            entries.add(first);
        }

        public void add(MMapEntry entry) {
            int index = Collections.binarySearch(entries, entry);
            if (index < 0) {
                entries.add(-index - 1, entry);
            } else {
                MMapEntry old = entries.get(index);
                if (old.perms == entry.perms) {
                    if (old.size < entry.size) {
                        entries.set(index, entry);
                    }
                } else {
                    entries.add(index, entry);
                }
            }
        }

        public Pointer find(long position, long length, Filter f) {
            int max_index = Collections.binarySearch(entries,
                    new MMapEntry(position, 0, 0, 0));
            max_index = max_index < 0 ? -max_index - 2 : max_index;
            for (int i = max_index; i >= 0; i--) {
                MMapEntry entry = entries.get(i);
                if (entry.file_end() >= position + length && f.filter(entry)) {
                    return entry.pointer().addOffset(position - entry.file_start());
                }
            }
            throw new IllegalArgumentException("can`t find data of length "
                    + length + " for position " + position);
        }

        public Pointer find(long position, long length) {
            return find(position, length, Filter.DEFAULT);
        }

        public Pointer findGenerated(long position, long length) {
            return find(position, length, Filter.GENERATED);
        }

        @Override
        public int compareTo(String other) {
            return String.CASE_INSENSITIVE_ORDER.compare(path, other);
        }

        @Override
        public String toString() {
            return "MMapFile{" + path + " " + entries + "}";
        }
    }

    private static final Pattern PATTERN = Pattern.compile(
            "(?<start>[0-9A-Fa-f]+)-(?<end>[0-9A-Fa-f]+)\\s+"
                    + "(?<perms>[rwxsp\\-]{4})\\s+"
                    + "(?<offset>[0-9A-Fa-f]+)\\s+"
                    + "[0-9A-Fa-f]+:[0-9A-Fa-f]+\\s+" // dev
                    + "[0-9]+\\s+" // inode
                    + "(?<path>/\\S+)");

    private static MMapFile[] read(String pid) {
        File maps = new File("/proc/" + pid + "/maps");
        if (!maps.isFile()) {
            throw new IllegalArgumentException("file " + maps + " does not exist");
        }
        String data;
        try {
            byte[] tmp = Files.readAllBytes(maps.toPath());
            data = new String(tmp);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

        ArrayList<MMapFile> out = new ArrayList<>();
        Matcher match = PATTERN.matcher(data);
        while (match.find()) {
            String path = match.group("path");
            String perms = match.group("perms");
            long start = Long.parseLong(match.group("start"), 16);
            long end = Long.parseLong(match.group("end"), 16);
            long size = end - start;
            long offset = Long.parseLong(match.group("offset"), 16);
            int i_perms = (perms.indexOf('r') < 0 ? 0 : PERMISSION_READ)
                    | (perms.indexOf('w') < 0 ? 0 : PERMISSION_WRITE)
                    | (perms.indexOf('x') < 0 ? 0 : PERMISSION_EXECUTE)
                    | (perms.indexOf('s') < 0 ? 0 : PERMISSION_SHARED)
                    | (perms.indexOf('p') < 0 ? 0 : PERMISSION_PRIVATE);

            MMapEntry tmp = new MMapEntry(offset, size, i_perms, start);
            int index = Collections.binarySearch(out, path);
            if (index < 0) {
                out.add(-index - 1, new MMapFile(path, tmp));
            } else {
                out.get(index).add(tmp);
            }
        }

        return out.stream().toArray(MMapFile[]::new);
    }

    public static MMapFile[] readSelf() {
        return read("self");
    }

    public static MMapFile[] readPid(int pid) {
        return read(Integer.toString(pid));
    }
}
