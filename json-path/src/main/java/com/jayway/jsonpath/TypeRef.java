package com.jayway.jsonpath;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeRef<T> implements Comparable<TypeRef<T>> {
    protected final Type type;

    protected TypeRef() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof Class<?>) {
            throw new IllegalArgumentException("No type info in TypeRef");
        }
        type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }

    @Override
    public int compareTo(TypeRef<T> o) {
        return 0;
    }
}
