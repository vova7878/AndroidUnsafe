package com.v7878.unsafe.methodhandle;

import static com.v7878.unsafe.AndroidUnsafe3.*;
import static com.v7878.unsafe.Utils.*;
import com.v7878.unsafe.dex.TypeId;
import java.lang.invoke.*;
import java.lang.reflect.*;
import java.nio.*;

public final class EmulatedStackFrame {

    public static final int RETURN_VALUE_IDX = -2;

    private static boolean is64BitPrimitive(Class<?> type) {
        return type == double.class || type == long.class;
    }

    public static int getSize(Class<?> type) {
        if (!type.isPrimitive()) {
            throw new IllegalArgumentException("type.isPrimitive() == false: " + type);
        }
        return is64BitPrimitive(type) ? 8 : 4;
    }

    private static void checkAssignable(Class<?> expectedType, Class<?> actualType) {
        if (!expectedType.isAssignableFrom(actualType)) {
            throw new IllegalArgumentException("Incorrect type: " + actualType
                    + ", expected: " + expectedType);
        }
    }

    static final Class<?> esf_class = nothrows_run(() -> {
        return Class.forName("dalvik.system.EmulatedStackFrame");
    });

    public static EmulatedStackFrame wrap(Object esf) {
        esf.getClass().asSubclass(esf_class);
        return new EmulatedStackFrame(esf);
    }

    private static final Method esf_create = nothrows_run(() -> {
        Method out = getDeclaredMethod(esf_class, "create", MethodType.class);
        setAccessible(out, true);
        return out;
    });

    public static EmulatedStackFrame create(MethodType frameType) {
        return new EmulatedStackFrame(nothrows_run(
                () -> esf_create.invoke(null, frameType)));
    }

    final Object esf;

    private EmulatedStackFrame(Object esf) {
        this.esf = esf;
    }

    private static final Field references = nothrows_run(() -> {
        Field out = getDeclaredField(esf_class, "references");
        setAccessible(out, true);
        return out;
    });

    public Object[] references() {
        return (Object[]) nothrows_run(() -> references.get(esf));
    }

    private static final Field stackFrame = nothrows_run(() -> {
        Field out = getDeclaredField(esf_class, "stackFrame");
        setAccessible(out, true);
        return out;
    });

    public byte[] stackFrame() {
        return (byte[]) nothrows_run(() -> stackFrame.get(esf));
    }

    private static final Field type = nothrows_run(() -> {
        Field out = getDeclaredField(esf_class, "type");
        setAccessible(out, true);
        return out;
    });

    public MethodType type() {
        return (MethodType) nothrows_run(() -> type.get(esf));
    }

    public StackFrameWriter createWriter() {
        StackFrameWriter out = new StackFrameWriter();
        out.attach(this);
        return out;
    }

    public StackFrameReader createReader() {
        StackFrameReader out = new StackFrameReader();
        out.attach(this);
        return out;
    }

    /*public void copyArgumentsTo(int startIdx, EmulatedStackFrame other,
            int otherStartIdx, int size) {
        checkFromIndexSize(startIdx, size,
                getMethodType().parameterCount());
        checkFromIndexSize(otherStartIdx, size,
                other.getMethodType().parameterCount());
    }*/
    public void copyReturnValueTo(EmulatedStackFrame other) {
        final Class<?> returnType = type().returnType();
        checkAssignable(other.type().returnType(), returnType);
        if (returnType.isPrimitive()) {
            byte[] this_stack = stackFrame();
            byte[] other_stack = other.stackFrame();
            int size = getSize(returnType);
            System.arraycopy(this_stack, this_stack.length - size,
                    other_stack, other_stack.length - size, size);
        } else {
            Object[] this_references = references();
            Object[] other_references = other.references();
            other_references[other_references.length - 1]
                    = this_references[this_references.length - 1];
        }
    }

    public static void copyNext(StackFrameReader reader,
            StackFrameWriter writer, Class<?> type) {
        switch (TypeId.of(type).getShorty()) {
            case 'L':
                writer.putNextReference(reader.nextReference(type), type);
                break;
            case 'Z':
                writer.putNextBoolean(reader.nextBoolean());
                break;
            case 'B':
                writer.putNextByte(reader.nextByte());
                break;
            case 'C':
                writer.putNextChar(reader.nextChar());
                break;
            case 'S':
                writer.putNextShort(reader.nextShort());
                break;
            case 'I':
                writer.putNextInt(reader.nextInt());
                break;
            case 'J':
                writer.putNextLong(reader.nextLong());
                break;
            case 'F':
                writer.putNextFloat(reader.nextFloat());
                break;
            case 'D':
                writer.putNextDouble(reader.nextDouble());
                break;
        }
    }

    private static class StackFrameAccessor {

        protected int referencesOffset;
        protected int argumentIdx;

        int[] frameOffsets;
        int[] referencesOffsets;

        protected ByteBuffer frameBuf;
        protected EmulatedStackFrame frame;

        protected StackFrameAccessor() {
            referencesOffset = 0;
            argumentIdx = 0;
            frameBuf = null;
        }

        public void attach(EmulatedStackFrame stackFrame) {
            if (frame != stackFrame) {
                // Re-initialize storage if not re-attaching to the same stackFrame.
                frame = stackFrame;
                frameBuf = ByteBuffer.wrap(frame.stackFrame())
                        .order(ByteOrder.LITTLE_ENDIAN);
                buildTables(stackFrame.type());
            }
            referencesOffset = 0;
            argumentIdx = 0;
        }

        private void buildTables(MethodType methodType) {
            final Class<?>[] ptypes = methodType.parameterArray();
            frameOffsets = new int[ptypes.length];
            referencesOffsets = new int[ptypes.length];
            int frameOffset = 0;
            int referenceOffset = 0;
            for (int i = 0; i < ptypes.length; ++i) {
                frameOffsets[i] = frameOffset;
                referencesOffsets[i] = referenceOffset;
                final Class<?> ptype = ptypes[i];
                if (ptype.isPrimitive()) {
                    frameOffset += getSize(ptype);
                } else {
                    referenceOffset += 1;
                }
            }
        }

        public Class<?> getCurrentArgumentType() {
            if (argumentIdx >= frame.type().parameterCount()
                    || argumentIdx == (RETURN_VALUE_IDX + 1)) {
                throw new IllegalArgumentException("Invalid argument index: " + argumentIdx);
            }
            MethodType type = frame.type();
            return (argumentIdx == RETURN_VALUE_IDX)
                    ? type.returnType() : type.parameterType(argumentIdx);
        }

        public void checkWriteType(Class<?> type) {
            checkAssignable(getCurrentArgumentType(), type);
        }

        public void checkReadType(Class<?> expectedType) {
            checkAssignable(expectedType, getCurrentArgumentType());
        }

        public StackFrameAccessor moveTo(int argumentIndex) {
            if (argumentIndex == RETURN_VALUE_IDX) {
                makeReturnValueAccessor();
            } else {
                referencesOffset = referencesOffsets[argumentIndex];
                frameBuf.position(frameOffsets[argumentIndex]);
                argumentIdx = argumentIndex;
            }
            return this;
        }

        private void makeReturnValueAccessor() {
            Class<?> rtype = frame.type().returnType();
            argumentIdx = RETURN_VALUE_IDX;
            // Position the cursor appropriately. The return value is either the last element
            // of the references array, or the last 4 or 8 bytes of the stack frame.
            if (rtype.isPrimitive()) {
                frameBuf.position(frameBuf.capacity() - getSize(rtype));
            } else {
                referencesOffset = frame.references().length - 1;
            }
        }
    }

    public static final class StackFrameWriter extends StackFrameAccessor {

        public void putNextByte(byte value) {
            checkWriteType(byte.class);
            argumentIdx++;
            frameBuf.putInt(value);
        }

        public void putNextInt(int value) {
            checkWriteType(int.class);
            argumentIdx++;
            frameBuf.putInt(value);
        }

        public void putNextLong(long value) {
            checkWriteType(long.class);
            argumentIdx++;
            frameBuf.putLong(value);
        }

        public void putNextChar(char value) {
            checkWriteType(char.class);
            argumentIdx++;
            frameBuf.putInt((int) value);
        }

        public void putNextBoolean(boolean value) {
            checkWriteType(boolean.class);
            argumentIdx++;
            frameBuf.putInt(value ? 1 : 0);
        }

        public void putNextShort(short value) {
            checkWriteType(short.class);
            argumentIdx++;
            frameBuf.putInt((int) value);
        }

        public void putNextFloat(float value) {
            checkWriteType(float.class);
            argumentIdx++;
            frameBuf.putFloat(value);
        }

        public void putNextDouble(double value) {
            checkWriteType(double.class);
            argumentIdx++;
            frameBuf.putDouble(value);
        }

        public void putNextReference(Object value, Class<?> expectedType) {
            checkWriteType(expectedType);
            argumentIdx++;
            frame.references()[referencesOffset++] = value;
        }

        @Override
        public StackFrameWriter moveTo(int argumentIndex) {
            return (StackFrameWriter) super.moveTo(argumentIndex);
        }
    }

    public static final class StackFrameReader extends StackFrameAccessor {

        public byte nextByte() {
            checkReadType(byte.class);
            argumentIdx++;
            return (byte) frameBuf.getInt();
        }

        public int nextInt() {
            checkReadType(int.class);
            argumentIdx++;
            return frameBuf.getInt();
        }

        public long nextLong() {
            checkReadType(long.class);
            argumentIdx++;
            return frameBuf.getLong();
        }

        public char nextChar() {
            checkReadType(char.class);
            argumentIdx++;
            return (char) frameBuf.getInt();
        }

        public boolean nextBoolean() {
            checkReadType(boolean.class);
            argumentIdx++;
            return (frameBuf.getInt() != 0);
        }

        public short nextShort() {
            checkReadType(short.class);
            argumentIdx++;
            return (short) frameBuf.getInt();
        }

        public float nextFloat() {
            checkReadType(float.class);
            argumentIdx++;
            return frameBuf.getFloat();
        }

        public double nextDouble() {
            checkReadType(double.class);
            argumentIdx++;
            return frameBuf.getDouble();
        }

        public <T> T nextReference(Class<T> expectedType) {
            checkReadType(expectedType);
            argumentIdx++;
            return (T) frame.references()[referencesOffset++];
        }

        @Override
        public StackFrameReader moveTo(int argumentIndex) {
            return (StackFrameReader) super.moveTo(argumentIndex);
        }
    }
}
