package com.v7878.unsafe.function;

import static com.v7878.unsafe.AndroidUnsafe3.ARRAY_BYTE_BASE_OFFSET;
import static com.v7878.unsafe.AndroidUnsafe3.IS64BIT;
import static com.v7878.unsafe.AndroidUnsafe3.copyMemory;
import static com.v7878.unsafe.AndroidUnsafe3.getDeclaredMethod;
import static com.v7878.unsafe.AndroidUnsafe3.throwException;
import static com.v7878.unsafe.Utils.nothrows_run;
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
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;

//TODO: toString
public class NativeLibrary implements SymbolLookup {

    public static class DLInfo {

        public final String fname;
        public final Pointer fbase;
        public final String sname;
        public final Pointer saddr;

        public DLInfo(String fname, Pointer fbase,
                      String sname, Pointer saddr) {
            this.fname = fname;
            this.fbase = fbase;
            this.sname = sname;
            this.saddr = saddr;
        }
    }

    private static final GroupLayout Dl_info = Layout.structLayout(
            ADDRESS.withName("dli_fname"),
            ADDRESS.withName("dli_fbase"),
            ADDRESS.withName("dli_sname"),
            ADDRESS.withName("dli_saddr")
    );

    private static MMapFile findLibDL() {
        MMapFile libdl = null;
        MMapFile[] mm = MMap.readSelf();
        String linker_name = "/libdl.so";
        for (MMapFile tmp : mm) {
            if (tmp.path.endsWith(linker_name)) {
                if (libdl != null) {
                    throw new IllegalStateException("more than one libdl found");
                }
                libdl = tmp;
            }
        }
        if (libdl == null) {
            throw new IllegalStateException("libdl not found");
        }

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

    private static MethodHandle dlsym;

    private synchronized static void init_dlsym() {
        if (dlsym == null) {
            MMapFile libdl = findLibDL();
            ELF.SymTab st = ELF.readSymTab(libdl, true);
            dlsym = Linker.downcallHandle(st.findFunction("dlsym", libdl),
                    FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS));
        }
    }

    static Pointer dlsym(Addressable handle, String name) {
        if (dlsym == null) {
            init_dlsym();
        }
        Objects.requireNonNull(name);
        MemorySegment c_name = MemorySegment.allocateCString(name);
        final long RTLD_DEFAULT = IS64BIT ? 0L : -1L;
        Addressable p_handle = handle != null ? handle : new Pointer(RTLD_DEFAULT);
        Pointer out = (Pointer) nothrows_run(() -> dlsym.invoke(p_handle, c_name));
        return out.isNull() ? null : out;
    }

    private static MethodHandle dlerror;

    private synchronized static void init_dlerror() {
        if (dlerror == null) {
            dlerror = SymbolLookup.defaultLookup().lookupHandle("dlerror",
                    FunctionDescriptor.of(ADDRESS));
        }
    }

    static String dlerror() {
        if (dlerror == null) {
            init_dlerror();
        }
        Pointer msg = (Pointer) nothrows_run(() -> dlerror.invoke());
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

    private static MethodHandle dlopen;

    private synchronized static void init_dlopen() {
        if (dlopen == null) {
            dlopen = SymbolLookup.defaultLookup().
                    lookupHandle("dlopen", FunctionDescriptor.of(
                            ADDRESS, ADDRESS, C_INT));
        }
    }

    static Pointer dlopen(String path, int flags) {
        if (dlopen == null) {
            init_dlopen();
        }
        Pointer tmp = (Pointer) nothrows_run(() -> dlopen.invoke(
                MemorySegment.allocateCString(path), flags));
        return tmp.isNull() ? null : tmp;
    }

    static Pointer dlopen(String path) {
        final int RTLD_NOW = IS64BIT ? 0x2 : 0x0;
        final int RTLD_GLOBAL = IS64BIT ? 0x100 : 0x2;
        return dlopen(path, RTLD_NOW);
    }

    private static MethodHandle dlclose;

    private synchronized static void init_dlclose() {
        if (dlclose == null) {
            dlclose = SymbolLookup.defaultLookup().lookupHandle("dlclose",
                    FunctionDescriptor.of(C_INT, ADDRESS));
        }
    }

    static void dlclose(Addressable handle) {
        if (dlclose == null) {
            init_dlclose();
        }
        Object ignore = nothrows_run(() -> dlclose.invoke(handle));
    }

    private static MethodHandle dladdr;

    private synchronized static void init_dladdr() {
        if (dladdr == null) {
            dladdr = SymbolLookup.defaultLookup().lookupHandle("dladdr",
                    FunctionDescriptor.of(C_INT, ADDRESS, ADDRESS));
        }
    }

    public static DLInfo dladdr(Addressable symbol) {
        if (dladdr == null) {
            init_dladdr();
        }
        Objects.requireNonNull(symbol);
        MemorySegment info = Dl_info.allocateHeap();
        int status = (int) nothrows_run(() -> dladdr.invoke(symbol, info));
        if (status == 0) {
            throw new IllegalArgumentException(dlerror("can`t get symbol info"));
        }

        Pointer dli_fname = (Pointer) info.select(
                groupElement("dli_fname")).getValue();
        String fname = dli_fname.isNull() ? null : dli_fname.getCString();

        Pointer fbase = (Pointer) info.select(
                groupElement("dli_fbase")).getValue();
        fbase = fbase.isNull() ? null : fbase;

        Pointer dli_sname = (Pointer) info.select(
                groupElement("dli_sname")).getValue();
        String sname = dli_sname.isNull() ? null : dli_sname.getCString();

        Pointer saddr = (Pointer) info.select(
                groupElement("dli_saddr")).getValue();
        saddr = saddr.isNull() ? null : saddr;

        return new DLInfo(fname, fbase, sname, saddr);
    }

    private static Method findLibrary;

    private synchronized static void init_findLibrary() {
        if (findLibrary == null) {
            findLibrary = getDeclaredMethod(ClassLoader.class,
                    "findLibrary", String.class);
        }
    }

    private static String[] systemLibPaths;

    private static void init_lib_paths() {
        if (systemLibPaths == null) {
            String javaLibraryPath = System.getProperty("java.library.path");
            if (javaLibraryPath == null) {
                systemLibPaths = new String[0];
                return;
            }
            String[] paths = javaLibraryPath.split(":");
            // Add a '/' to the end of each directory so we don't have to do it every time.
            for (int i = 0; i < paths.length; ++i) {
                if (!paths[i].endsWith("/")) {
                    paths[i] += "/";
                }
            }
            systemLibPaths = paths;
        }
    }

    private static String[] getSystemLibPaths() {
        if (systemLibPaths == null) {
            init_lib_paths();
        }
        return systemLibPaths;
    }

    public static String findLibraryPath(ClassLoader loader, String name) {
        String out = null;
        if (loader != null) {
            if (findLibrary == null) {
                init_findLibrary();
            }
            out = (String) nothrows_run(
                    () -> findLibrary.invoke(loader, name));
        }
        if (out != null) {
            return out;
        }
        String map_name = System.mapLibraryName(name);
        for (String path : getSystemLibPaths()) {
            path += map_name;
            if (new File(path).isFile()) {
                out = path;
                break;
            }
        }
        return out;
    }

    private final Object LOCK = new Object();
    private final String name;
    private Pointer handle;

    private NativeLibrary(Pointer handle, String name) {
        this.handle = handle;
        this.name = name;
    }

    public static NativeLibrary load(String path) {
        Objects.requireNonNull(path);
        Pointer tmp = dlopen(path);
        if (tmp == null) {
            throw new IllegalArgumentException(
                    dlerror("Can`t find library " + path));
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
            throw new IllegalArgumentException(
                    dlerror("Can`t find symbol "
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
