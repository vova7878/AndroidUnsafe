package com.v7878.unsafe.bytecode;

import static com.v7878.unsafe.bytecode.DexConstants.*;
import com.v7878.unsafe.bytecode.EncodedValue.ArrayValue;
import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public class CallSiteId {

    public static final int SIZE = 0x04;

    public ArrayValue value;

    public static CallSiteId read(RandomInput in, ReadContext context) {
        CallSiteId out = new CallSiteId();
        RandomInput in2 = in.duplicate(in.readInt());
        out.value = (ArrayValue) EncodedValueReader
                .readValue(in2, context, VALUE_ARRAY);
        return out;
    }

    public void fillContext(DataSet data) {
        value.fillContext(data);
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
