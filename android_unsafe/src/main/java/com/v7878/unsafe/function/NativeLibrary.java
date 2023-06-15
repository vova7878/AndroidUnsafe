package com.v7878.unsafe.function;

import static com.v7878.unsafe.AndroidUnsafe3.ARRAY_BYTE_BASE_OFFSET;
import static com.v7878.unsafe.AndroidUnsafe3.IS64BIT;
import static com.v7878.unsafe.AndroidUnsafe3.copyMemory;
import static com.v7878.unsafe.AndroidUnsafe3.getDeclaredMethod;
import static com.v7878.unsafe.AndroidUnsafe3.throwException;
import static com.v7878.unsafe.AndroidUnsafe3.unreflect;
import static com.v7878.unsafe.AndroidUnsafe3.unreflectDirect;
import static com.v7878.unsafe.Utils.nothrows_run;
import static com.v7878.unsafe.Utils.runOnce;
import static com.v7878.unsafe.memory.Bindable.CSTRING;
import static com.v7878.unsafe.memory.LayoutPath.PathElement.groupElement;
import static com.v7878.unsafe.memory.PlatformLayouts.C_INT;
import static com.v7878.unsafe.memory.ValueLayout.ADDRESS;

import com.v7878.unsafe.function.MMap.MMapEntry;
import com.v7878.unsafe.function.MMap.MMapFile;
import com.v7878.unsafe.memory.Addressable;
import com.v7878.unsafe.memory.GroupLayout;
import com.v7878.unsafe.memory.Layout;
import com.v7878.unsafe.memory.MemorySegment;
import com.v7878.unsafe.memory.Pointer;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

//TODO: toString
public class NativeLibrary implements SymbolLookup {

    public static class DLInfo {

        public final String fname;
        public final Pointer fbase;
        public final String sname;
        public final Pointer saddr;

        public DLInfo(String fname, Pointer fbase, String sname, Pointer saddr) {
            this.fname = fname;
            this.fbase = fbase;
            this.sname = sname;
            this.saddr = saddr;
        }
    }

    private static final GroupLayout Dl_info = Layout.structLayout(
            ADDRESS.withName("dli_fname").withContent(CSTRING),
            ADDRESS.withName("dli_fbase"),
            ADDRESS.withName("dli_sname").withContent(CSTRING),
            ADDRESS.withName("dli_saddr")
    );

    private static MMapFile findLibDL() {
        MMapFile libdl = null;
        MMapFile[] mm = MMap.readSelf();
        String name = "/libdl.so";
        for (MMapFile tmp : mm) {
            if (tmp.path.endsWith(name)) {
                if (libdl != null) {
                    throw new IllegalStateException("more than one libdl found");
                }
                libdl = tmp;
            }
        }
        if (libdl == null) {
            throw new IllegalStateException("libdl not found");
        }

        //TODO: code cleanup
        try {
            byte[] tmp = Files.readAllBytes(new File(libdl.path).toPath());
            Pointer data = Pointer.allocateHeap(tmp.length, 8);
            copyMemory(tmp, ARRAY_BYTE_BASE_OFFSET,
                    data.getBase(), data.getOffset(), tmp.length);
            libdl.add(new MMapEntry(0, tmp.length,
                    MMap.PERMISSION_GENERATED, data));
        } catch (IOException ex) {
            throwException(ex);
        }
        return libdl;
    }

    private static final Supplier<MethodHandle> dlsym = runOnce(() -> {
        MMapFile libdl = findLibDL();
        ELF.SymTab st = ELF.readSymTab(libdl, true);
        return Linker.downcallHandle(st.findFunction("dlsym", libdl),
                FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS));
    });

    static Pointer dlsym(Addressable handle, String name) {
        Objects.requireNonNull(name);
        MemorySegment c_name = MemorySegment.allocateCString(name);
        final long RTLD_DEFAULT = IS64BIT ? 0L : -1L;
        Addressable p_handle = handle != null ? handle : new Pointer(RTLD_DEFAULT);
        Pointer out = (Pointer) nothrows_run(() -> dlsym.get().invoke(p_handle, c_name));
        return out.isNull() ? null : out;
    }

    private static final Supplier<MethodHandle> dlerror = runOnce(
            () -> SymbolLookup.defaultLookup().lookupHandle("dlerror",
                    FunctionDescriptor.of(ADDRESS)));

    static String dlerror() {
        Pointer msg = (Pointer) nothrows_run(() -> dlerror.get().invoke());
        return msg.isNull() ? null : msg.getCString();
    }

    static String dlerror(String msg) {
        StringBuilder out = new StringBuilder();
        out.append(msg);
        String err = dlerror();
        if (err == null) {
            out.append(", no dlerror message");
        } else {
            out.append(" dlerror: ");
            out.append(err);
        }
        return out.toString();
    }

    private static final Supplier<MethodHandle> dlopen = runOnce(
            () -> SymbolLookup.defaultLookup().lookupHandle("dlopen",
                    FunctionDescriptor.of(ADDRESS, ADDRESS, C_INT)));

    static Pointer dlopen(String path, int flags) {
        Pointer tmp = (Pointer) nothrows_run(() -> dlopen.get().invoke(
                MemorySegment.allocateCString(path), flags));
        return tmp.isNull() ? null : tmp;
    }

    static Pointer dlopen(String path) {
        final int RTLD_NOW = IS64BIT ? 0x2 : 0x0;
        final int RTLD_GLOBAL = IS64BIT ? 0x100 : 0x2;
        return dlopen(path, RTLD_NOW);
    }

    private static final Supplier<MethodHandle> dlclose = runOnce(
            () -> SymbolLookup.defaultLookup().lookupHandle("dlclose",
                    FunctionDescriptor.of(C_INT, ADDRESS)));

    static void dlclose(Addressable handle) {
        Object ignore = nothrows_run(() -> dlclose.get().invoke(handle));
    }

    private static final Supplier<MethodHandle> dladdr = runOnce(
            () -> SymbolLookup.defaultLookup().lookupHandle("dladdr",
                    FunctionDescriptor.of(C_INT, ADDRESS, ADDRESS)));

    public static DLInfo dladdr(Addressable symbol) {
        Objects.requireNonNull(symbol);
        MemorySegment info = Dl_info.allocateHeap();
        int status = (int) nothrows_run(() -> dladdr.get().invoke(symbol, info));
        if (status == 0) {
            throw new IllegalArgumentException(dlerror("can`t get symbol info"));
        }

        String fname = (String) info.select(groupElement("dli_fname")).getValue();

        Pointer fbase = (Pointer) info.select(groupElement("dli_fbase")).getValue();
        fbase = fbase.isNull() ? null : fbase;

        String sname = (String) info.select(groupElement("dli_sname")).getValue();

        Pointer saddr = (Pointer) info.select(groupElement("dli_saddr")).getValue();
        saddr = saddr.isNull() ? null : saddr;

        return new DLInfo(fname, fbase, sname, saddr);
    }

    private static final Supplier<MethodHandle> findLibrary = runOnce(
            () -> unreflect(getDeclaredMethod(ClassLoader.class,
                    "findLibrary", String.class)));

    private static final Supplier<String[]> systemLibPaths = runOnce(() -> {
        String javaLibraryPath = System.getProperty("java.library.path");
        if (javaLibraryPath == null) {
            return new String[0];
        }
        String[] paths = javaLibraryPath.split(":");
        // Add a '/' to the end of each directory so we don't have to do it every time.
        for (int i = 0; i < paths.length; ++i) {
            if (!paths[i].endsWith("/")) {
                paths[i] += "/";
            }
        }
        return paths;
    });

    public static String findLibraryPath(ClassLoader loader, String name) {
        if (loader != null) {
            String tmp = (String) nothrows_run(() -> findLibrary.get().invoke(loader, name));
            if (tmp != null) {
                return tmp;
            }
        }
        String map_name = System.mapLibraryName(name);
        for (String path : systemLibPaths.get()) {
            path += map_name;
            if (new File(path).isFile()) {
                return path;
            }
        }
        return null;
    }

    private final Object LOCK = new Object();
    private final String name;
    private volatile Pointer handle;

    private NativeLibrary(Pointer handle, String name) {
        this.handle = handle;
        this.name = name;
    }

    public static NativeLibrary load(String path) {
        Objects.requireNonNull(path);
        Pointer tmp = dlopen(path);
        if (tmp == null) {
            throw new IllegalArgumentException(dlerror("Can`t find library " + path));
        }
        return new NativeLibrary(tmp, path);
    }

    public static NativeLibrary loadLibrary(ClassLoader loader, String name) {
        if (name.indexOf(File.separatorChar) != -1) {
            throw new IllegalArgumentException(
                    "Directory separator should not appear in library name: " + name);
        }
        String filename = findLibraryPath(loader, name);
        if (filename == null) {
            filename = System.mapLibraryName(name);
        }
        return load(filename);
    }

    public String name() {
        return name;
    }

    public Pointer handle() {
        if (handle == null) {
            throw new IllegalStateException("library is closed");
        }
        return handle;
    }

    @Override
    public Optional<Pointer> find(String name) {
        Objects.requireNonNull(name);
        synchronized (LOCK) {
            Pointer tmp = dlsym(handle(), name);
            return Optional.ofNullable(tmp);
        }
    }

    @Override
    public Pointer lookup(String name) {
        Optional<Pointer> addr = find(name);
        if (!addr.isPresent()) {
            throw new IllegalArgumentException(dlerror("Can`t find symbol "
                    + name + " in library " + name()));
        }
        return addr.get();
    }

    @Override
    public void close() {
        if (handle == null) {
            return;
        }
        Pointer tmp;
        synchronized (LOCK) {
            if (handle == null) {
                return;
            }
            tmp = handle;
            handle = null;
        }
        dlclose(tmp);
    }
}
