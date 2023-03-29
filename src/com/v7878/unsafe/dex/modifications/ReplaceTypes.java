package com.v7878.unsafe.dex.modifications;

import com.v7878.unsafe.dex.*;
import java.util.HashMap;
import java.util.Objects;

public class ReplaceTypes extends DataFilter {

    private final HashMap<TypeId, TypeId> rules;

    public ReplaceTypes() {
        rules = new HashMap<>();
    }

    public TypeId putRule(TypeId from, TypeId to) {
        Objects.requireNonNull(from, "\"from\" can`t be null");
        Objects.requireNonNull(to, "\"to\" can`t be null");
        return rules.put(from, to);
    }

    @Override
    public void add(TypeId value) {
        TypeId to = rules.get(value);
        if (to != null) {
            value.setDescriptor(to.getDescriptor());
        }
        super.add(value);
    }
}
