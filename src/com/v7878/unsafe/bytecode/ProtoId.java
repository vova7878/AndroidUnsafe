package com.v7878.unsafe.bytecode;

import com.v7878.unsafe.io.RandomInput;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProtoId {

    public StringId shorty;
    public TypeId return_type;
    public TypeId[] parameters;

    public static ProtoId read(RandomInput in, Context context) {
        ProtoId out = new ProtoId();
        out.shorty = context.string(in.readInt());
        out.return_type = context.type(in.readInt());
        int parameters_off = in.readInt();
        if (parameters_off != 0) {
            out.parameters = TypeId.readTypeList(in.duplicate(parameters_off), context);
        } else {
            out.parameters = new TypeId[0];
        }
        return out;
    }

    @Override
    public String toString() {
        String params = Arrays.stream(parameters)
                .map((p) -> p.toString())
                .collect(Collectors.joining("", "(", ")"));
        return params + return_type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProtoId) {
            ProtoId pobj = (ProtoId) obj;
            return Objects.equals(shorty, pobj.shorty)
                    && Objects.equals(return_type, pobj.return_type)
                    && Arrays.equals(parameters, pobj.parameters);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shorty, return_type, Arrays.hashCode(parameters));
    }
}
