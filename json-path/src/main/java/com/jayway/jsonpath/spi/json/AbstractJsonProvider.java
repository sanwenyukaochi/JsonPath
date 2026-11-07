package com.jayway.jsonpath.spi.json;

import com.jayway.jsonpath.JsonPathException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractJsonProvider implements JsonProvider {

    public boolean isArray(Object obj) {
        return (obj instanceof List);
    }

    public Object getArrayIndex(Object obj, int idx) {
        return ((List) obj).get(idx);
    }

    @Deprecated
    public final Object getArrayIndex(Object obj, int idx, boolean unwrap) {
        return getArrayIndex(obj, idx);
    }

    public void setArrayIndex(Object array, int index, Object newValue) {
        if (!isArray(array)) {
            throw new UnsupportedOperationException();
        } else {
            List l = (List) array;
            if (index == l.size()) {
                l.add(newValue);
            } else {
                l.set(index, newValue);
            }
        }
    }

    public Object getMapValue(Object obj, String key) {
        Map m = (Map) obj;
        if (!m.containsKey(key)) {
            return JsonProvider.UNDEFINED;
        } else {
            return m.get(key);
        }
    }

    @SuppressWarnings("unchecked")
    public void setProperty(Object obj, Object key, Object value) {
        if (isMap(obj)) ((Map) obj).put(key.toString(), value);
        else {
            throw new JsonPathException(
                    "setProperty operation cannot be used with " + obj != null
                            ? obj.getClass().getName()
                            : "null");
        }
    }

    @SuppressWarnings("unchecked")
    public void removeProperty(Object obj, Object key) {
        if (isMap(obj)) ((Map) obj).remove(key.toString());
        else {
            List list = (List) obj;
            int index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
            list.remove(index);
        }
    }

    public boolean isMap(Object obj) {
        return (obj instanceof Map);
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getPropertyKeys(Object obj) {
        if (isArray(obj)) {
            throw new UnsupportedOperationException();
        } else {
            return ((Map) obj).keySet();
        }
    }

    public int length(Object obj) {
        if (isArray(obj)) {
            return ((List) obj).size();
        } else if (isMap(obj)) {
            return getPropertyKeys(obj).size();
        } else if (obj instanceof String) {
            return ((String) obj).length();
        }
        throw new JsonPathException(
                "length operation cannot be applied to "
                        + (obj != null ? obj.getClass().getName() : "null"));
    }

    @SuppressWarnings("unchecked")
    public Iterable<?> toIterable(Object obj) {
        if (isArray(obj)) return ((Iterable) obj);
        else
            throw new JsonPathException(
                    "Cannot iterate over " + obj != null ? obj.getClass().getName() : "null");
    }

    @Override
    public Object unwrap(Object obj) {
        return obj;
    }
}
