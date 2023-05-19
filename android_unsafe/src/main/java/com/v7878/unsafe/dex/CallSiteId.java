package com.v7878.unsafe.dex;

import static com.v7878.unsafe.dex.DexConstants.VALUE_ARRAY;

import com.v7878.unsafe.dex.EncodedValue.ArrayValue;
import com.v7878.unsafe.io.RandomInput;

import java.util.Objects;

public class CallSiteId implements PublicCloneable {

    public static final int SIZE = 0x04;

    private ArrayValue value;

    public CallSiteId(ArrayValue value) {
        setValue(value);
    }

    public final void setValue(ArrayValue value) {
        this.value = Objects.requireNonNull(value,
                "call site value can`t be null").clone();
    }

    public final ArrayValue getValue() {
        return value;
    }

    public static CallSiteId read(RandomInput in, ReadContext context) {
        RandomInput in2 = in.duplicate(in.readInt());
        ArrayValue value = (ArrayValue) EncodedValueReader
                .readValue(in2, context, VALUE_ARRAY);
        return new CallSiteId(value);
    }

    public void collectData(DataCollector data) {
        data.fill(value);
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

    @Override
    public CallSiteId clone() {
        return new CallSiteId(value);
    }
}
