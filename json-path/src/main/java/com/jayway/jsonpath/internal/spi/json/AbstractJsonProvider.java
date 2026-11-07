package com.jayway.jsonpath.internal.spi.json;

import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.spi.json.JsonProvider;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJsonProvider implements JsonProvider {

    private static final Logger logger = LoggerFactory.getLogger(AbstractJsonProvider.class);

    public boolean isArray(Object obj) {
        return (obj instanceof List);
    }

    public Object getArrayIndex(Object obj, int idx) {
        return ((List) obj).get(idx);
    }

    public final Object getArrayIndex(Object obj, int idx, boolean unwrap) {
        return getArrayIndex(obj, idx);
    }

    public void setArrayIndex(Object array, int index, Object newValue) {
        if (!isArray(array)) {
            throw new UnsupportedOperationException();
        } else {
            ((List) array).set(index, newValue);
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
            List list = (List) obj;
            int index;
            if (key != null) {
                index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
            } else {
                index = list.size();
            }
            list.add(index, value);
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
                "length operation can not applied to " + obj != null
                        ? obj.getClass().getName()
                        : "null");
    }

    @SuppressWarnings("unchecked")
    public Iterable<? extends Object> toIterable(Object obj) {
        if (isArray(obj)) return ((Iterable) obj);
        else return ((Map) obj).values();
    }

    @Override
    public Object unwrap(Object obj) {
        return obj;
    }
}
