package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.bytecode.DexConstants.*;
import com.v7878.unsafe.bytecode.EncodedValue.ArrayValue;
import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public class CallSiteId {

    public ArrayValue value;

    public static CallSiteId read(RandomInput in, Context context) {
        CallSiteId out = new CallSiteId();
        RandomInput in2 = in.duplicate(in.readInt());
        EncodedValueReader reader = new EncodedValueReader(in2, VALUE_ARRAY);
        out.value = reader.readArray(context);
        return out;
    }

    @Override
    public String toString() {
        return "CallSiteId" + value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CallSiteId) {
            CallSiteId csobj = (CallSiteId) obj;
            return Objects.equals(value, csobj.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
