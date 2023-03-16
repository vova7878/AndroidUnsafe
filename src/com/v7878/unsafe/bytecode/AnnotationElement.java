package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.Objects;

public class AnnotationElement {

    public String name;
    public EncodedValue value;

    public static AnnotationElement read(RandomInput in, Context context) {
        AnnotationElement out = new AnnotationElement();
        out.name = context.string(in.readULeb128());
        EncodedValueReader reader = new EncodedValueReader(in);
        out.value = reader.readValue(context);
        return out;
    }

    void fillContext(DataSet data) {
        data.addString(name);
        value.fillContext(data);
    }

    @Override
    public String toString() {
        return "AnnotationElement{" + name + " = " + value + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AnnotationElement) {
            AnnotationElement aeobj = (AnnotationElement) obj;
            return Objects.equals(name, aeobj.name)
                    && Objects.equals(value, aeobj.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
