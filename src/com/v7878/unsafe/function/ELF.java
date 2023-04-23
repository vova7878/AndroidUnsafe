package com.v7878.unsafe.function;

import static com.v7878.unsafe.AndroidUnsafe.IS64BIT;
import com.v7878.unsafe.function.MMap.MMapEntry;
import com.v7878.unsafe.function.MMap.MMapFile;
import static com.v7878.unsafe.function.MMap.PERMISIION_GENERATED;
import com.v7878.unsafe.memory.*;
import static com.v7878.unsafe.memory.Layout.*;
import static com.v7878.unsafe.memory.LayoutPath.PathElement.*;
import static com.v7878.unsafe.memory.ValueLayout.*;
import java.util.Objects;

// see elf.h
public class ELF {

    public static final int EI_NIDENT = 16;
    public static final byte[] ELFMAG = {0x7f, 'E', 'L', 'F'};

    public static final int STT_FUNC = 2;

    public static final ValueLayout Elf32_Addr = JAVA_INT;
    public static final ValueLayout Elf32_Half = JAVA_SHORT;
    public static final ValueLayout Elf32_Off = JAVA_INT;
    public static final ValueLayout Elf32_Word = JAVA_INT;

    public static final ValueLayout Elf64_Addr = JAVA_LONG;
    public static final ValueLayout Elf64_Half = JAVA_SHORT;
    public static final ValueLayout Elf64_Off = JAVA_LONG;
    public static final ValueLayout Elf64_Word = JAVA_INT;
    public static final ValueLayout Elf64_Xword = JAVA_LONG;

    public static final GroupLayout Elf32_Ehdr = structLayout(
            sequenceLayout(EI_NIDENT, JAVA_BYTE).withName("e_ident"),
            Elf32_Half.withName("e_type"),
            Elf32_Half.withName("e_machine"),
            Elf32_Word.withName("e_version"),
            Elf32_Addr.withName("e_entry"),
            Elf32_Off.withName("e_phoff"),
            Elf32_Off.withName("e_shoff"),
            Elf32_Word.withName("e_flags"),
            Elf32_Half.withName("e_ehsize"),
            Elf32_Half.withName("e_phentsize"),
            Elf32_Half.withName("e_phnum"),
            Elf32_Half.withName("e_shentsize"),
            Elf32_Half.withName("e_shnum"),
            Elf32_Half.withName("e_shstrndx")
    );
    public static final GroupLayout Elf64_Ehdr = structLayout(
            sequenceLayout(EI_NIDENT, JAVA_BYTE).withName("e_ident"),
            Elf64_Half.withName("e_type"),
            Elf64_Half.withName("e_machine"),
            Elf64_Word.withName("e_version"),
            Elf64_Addr.withName("e_entry"),
            Elf64_Off.withName("e_phoff"),
            Elf64_Off.withName("e_shoff"),
            Elf64_Word.withName("e_flags"),
            Elf64_Half.withName("e_ehsize"),
            Elf64_Half.withName("e_phentsize"),
            Elf64_Half.withName("e_phnum"),
            Elf64_Half.withName("e_shentsize"),
            Elf64_Half.withName("e_shnum"),
            Elf64_Half.withName("e_shstrndx")
    );
    public static final GroupLayout Elf_Ehdr = IS64BIT ? Elf64_Ehdr : Elf32_Ehdr;

    public static final GroupLayout Elf32_Sym = structLayout(
            Elf32_Word.withName("st_name"),
            Elf32_Addr.withName("st_value"),
            Elf32_Word.withName("st_size"),
            JAVA_BYTE.withName("st_info"),
            JAVA_BYTE.withName("st_other"),
            Elf32_Half.withName("st_shndx")
    );
    public static final GroupLayout Elf64_Sym = structLayout(
            Elf64_Word.withName("st_name"),
            JAVA_BYTE.withName("st_info"),
            JAVA_BYTE.withName("st_other"),
            Elf64_Half.withName("st_shndx"),
            Elf64_Addr.withName("st_value"),
            Elf64_Xword.withName("st_size")
    );
    public static final GroupLayout Elf_Sym = IS64BIT ? Elf64_Sym : Elf32_Sym;

    public static final GroupLayout Elf32_Phdr = structLayout(
            Elf32_Word.withName("p_type"),
            Elf32_Off.withName("p_offset"),
            Elf32_Addr.withName("p_vaddr"),
            Elf32_Addr.withName("p_paddr"),
            Elf32_Word.withName("p_filesz"),
            Elf32_Word.withName("p_memsz"),
            Elf32_Word.withName("p_flags"),
            Elf32_Word.withName("p_align")
    );
    public static final GroupLayout Elf64_Phdr = structLayout(
            Elf64_Word.withName("p_type"),
            Elf64_Word.withName("p_flags"),
            Elf64_Off.withName("p_offset"),
            Elf64_Addr.withName("p_vaddr"),
            Elf64_Addr.withName("p_paddr"),
            Elf64_Xword.withName("p_filesz"),
            Elf64_Xword.withName("p_memsz"),
            Elf64_Xword.withName("p_align")
    );
    public static final GroupLayout Elf_Phdr = IS64BIT ? Elf64_Phdr : Elf32_Phdr;

    public static final GroupLayout Elf32_Shdr = structLayout(
            Elf32_Word.withName("sh_name"),
            Elf32_Word.withName("sh_type"),
            Elf32_Word.withName("sh_flags"),
            Elf32_Addr.withName("sh_addr"),
            Elf32_Off.withName("sh_offset"),
            Elf32_Word.withName("sh_size"),
            Elf32_Word.withName("sh_link"),
            Elf32_Word.withName("sh_info"),
            Elf32_Word.withName("sh_addralign"),
            Elf32_Word.withName("sh_entsize")
    );
    public static final GroupLayout Elf64_Shdr = structLayout(
            Elf64_Word.withName("sh_name"),
            Elf64_Word.withName("sh_type"),
            Elf64_Xword.withName("sh_flags"),
            Elf64_Addr.withName("sh_addr"),
            Elf64_Off.withName("sh_offset"),
            Elf64_Xword.withName("sh_size"),
            Elf64_Word.withName("sh_link"),
            Elf64_Word.withName("sh_info"),
            Elf64_Xword.withName("sh_addralign"),
            Elf64_Xword.withName("sh_entsize")
    );
    public static final GroupLayout Elf_Shdr = IS64BIT ? Elf64_Shdr : Elf32_Shdr;

    public static class Element {

        public final String name;
        public final MemorySegment data;

        public Element(String name, MemorySegment data) {
            this.name = name;
            this.data = data;
        }

        @Override
        public String toString() {
            return "\'" + name + "\' " + data.readToString();
        }
    }

    private static final long sh_offset = Elf_Shdr.selectPath(
            groupElement("sh_offset")).offset();
    private static final long sh_size = Elf_Shdr.selectPath(
            groupElement("sh_size")).offset();

    private static final long e_shentsize = Elf_Ehdr.selectPath(
            groupElement("e_shentsize")).offset();
    private static final long e_shnum = Elf_Ehdr.selectPath(
            groupElement("e_shnum")).offset();
    private static final long e_shoff = Elf_Ehdr.selectPath(
            groupElement("e_shoff")).offset();
    private static final long e_shstrndx = Elf_Ehdr.selectPath(
            groupElement("e_shstrndx")).offset();

    private static final long sh_name = Elf_Shdr.selectPath(
            groupElement("sh_name")).offset();

    private static final long st_name = Elf_Sym.selectPath(
            groupElement("st_name")).offset();
    private static final long st_info = Elf_Sym.selectPath(
            groupElement("st_info")).offset();
    private static final long st_value = Elf_Sym.selectPath(
            groupElement("st_value")).offset();
    private static final long st_size = Elf_Sym.selectPath(
            groupElement("st_size")).offset();

    public static MemorySegment getRawSegmentData(MMapFile in, MemorySegment segment) {
        long off = segment.get(WORD, sh_offset).ulongValue();
        long size = segment.get(WORD, sh_size).ulongValue();
        return Layout.rawLayout(size).bind(in.findGenerated(off, size));
    }

    public static Element[] readSegments(MMapFile in) {
        MemorySegment ehdr = Elf_Ehdr.bind(in.findGenerated(0, Elf_Ehdr.size()));
        if (ehdr.get(JAVA_BYTE, 0) != ELFMAG[0]
                || ehdr.get(JAVA_BYTE, 1) != ELFMAG[1]
                || ehdr.get(JAVA_BYTE, 2) != ELFMAG[2]
                || ehdr.get(JAVA_BYTE, 3) != ELFMAG[3]) {
            throw new IllegalArgumentException("not an elf");
        }
        int shentsize = ehdr.get(JAVA_CHAR, e_shentsize);
        if (Elf_Shdr.size() != shentsize) {
            throw new IllegalArgumentException("elf error: e_shentsize(="
                    + shentsize + ") != sizeof(Elf_Shdr)(=" + Elf_Shdr.size() + ")");
        }
        int shnum = ehdr.get(JAVA_CHAR, e_shnum);
        long shoff = ehdr.get(WORD, e_shoff).ulongValue();
        SequenceLayout all_sh_layout = Layout.sequenceLayout(shnum, Elf_Shdr);
        MemorySegment all_sh = all_sh_layout.bind(
                in.findGenerated(shoff, all_sh_layout.size()));
        int shstrndx = ehdr.get(JAVA_CHAR, e_shstrndx);
        MemorySegment strings = getRawSegmentData(in,
                all_sh.select(sequenceElement(shstrndx)));
        Element[] segments = new Element[shnum];
        for (int i = 0; i < shnum; i++) {
            MemorySegment segment = all_sh.select(sequenceElement(i));
            long name_off = segment.get(JAVA_INT, sh_name) & 0xffffffffL;
            String name = strings.getCString(name_off);
            segments[i] = new Element(name, segment);
        }
        return segments;
    }

    public static class SymTab {

        public final Element[] sym;
        public final Element[] dyn;

        public SymTab(Element[] sym, Element[] dyn) {
            this.sym = sym;
            this.dyn = dyn;
        }

        public Element find(String name) {
            Objects.requireNonNull(name);
            if (dyn != null) {
                for (Element tmp : dyn) {
                    if (name.equals(tmp.name)) {
                        return tmp;
                    }
                }
            }
            if (sym != null) {
                for (Element tmp : sym) {
                    if (name.equals(tmp.name)) {
                        return tmp;
                    }
                }
            }
            throw new IllegalArgumentException("symbol \'" + name + "\' not found");
        }

        public MemorySegment findFunction(String name, MMapFile in) {
            MemorySegment symbol = find(name).data;
            int type = symbol.get(JAVA_BYTE, st_info) & 0xf;
            if (type != STT_FUNC) {
                throw new IllegalArgumentException("unknown symbol type: " + type);
            }
            Pointer bias = new Pointer(in.entries.stream()
                    .filter(e -> (e.perms() != PERMISIION_GENERATED))
                    .mapToLong(MMapEntry::start).min().getAsLong());
            long value = symbol.get(WORD, st_value).ulongValue();
            long size = symbol.get(WORD, st_size).ulongValue();
            return Layout.rawLayout(size).bind(bias.addOffset(value));
        }
    }

    public static Element[] readSymbols(MMapFile in,
            MemorySegment symtab, MemorySegment strtab) {

        symtab = getRawSegmentData(in, symtab);
        strtab = getRawSegmentData(in, strtab);

        if (symtab.size() % Elf_Sym.size() != 0) {
            throw new IllegalArgumentException("elf error");
        }
        int num = (int) (symtab.size() / Elf_Sym.size());
        symtab = Layout.sequenceLayout(num,
                Elf_Sym).bind(symtab.pointer());

        Element[] symbols = new Element[num];
        for (int i = 0; i < num; i++) {
            MemorySegment symbol = symtab.select(sequenceElement(i));
            long name_off = symbol.get(JAVA_INT, st_name) & 0xffffffffL;
            String name = strtab.getCString(name_off);
            symbols[i] = new Element(name, symbol);
        }

        return symbols;
    }

    public static SymTab readSymTab(MMapFile in, boolean only_dyn) {
        Element[] segments = readSegments(in);

        Element symtab = null;
        Element strtab = null;
        Element dynsym = null;
        Element dynstr = null;

        for (Element segment : segments) {
            switch (segment.name) {
                case ".strtab":
                    if (!only_dyn) {
                        if (strtab != null) {
                            throw new IllegalArgumentException(
                                    "too many string tables");
                        }
                        strtab = segment;
                    }
                    break;
                case ".symtab":
                    if (!only_dyn) {
                        if (symtab != null) {
                            throw new IllegalArgumentException(
                                    "too many symbol tables");
                        }
                        symtab = segment;
                    }
                    break;
                case ".dynstr":
                    if (dynstr != null) {
                        throw new IllegalArgumentException(
                                "too many string tables");
                    }
                    dynstr = segment;
                    break;
                case ".dynsym":
                    if (dynsym != null) {
                        throw new IllegalArgumentException(
                                "too many symbol tables");
                    }
                    dynsym = segment;
                    break;
            }
        }

        Element[] sym = null;
        if ((symtab != null) && (strtab != null)) {
            sym = readSymbols(in, symtab.data, strtab.data);
        }

        Element[] dyn = null;
        if ((dynsym != null) && (dynstr != null)) {
            dyn = readSymbols(in, dynsym.data, dynstr.data);
        }

        return new SymTab(sym, dyn);
    }
}
