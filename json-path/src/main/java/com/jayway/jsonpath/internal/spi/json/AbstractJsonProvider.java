package com.jayway.jsonpath.internal.spi.json;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.Utils;
import com.jayway.jsonpath.spi.json.JsonProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractJsonProvider implements JsonProvider {

    @Override
    public Object clone(Object obj) {
        return Utils.clone((Serializable) obj);
    }

    public boolean isContainer(Object obj) {
        return (isArray(obj) || isMap(obj));
    }

    public boolean isArray(Object obj) {
        return (obj instanceof List);
    }

    public Object getArrayIndex(Object obj, int idx) {
        return ((List) obj).get(idx);
    }

    public Object getMapValue(Object obj, String key) {
        return ((Map) obj).get(key);
    }

    public Object getMapValue(Object obj, String key, boolean throwOnMissing) {
        Map m = (Map) obj;
        if (!m.containsKey(key)) {
            throw new PathNotFoundException(
                    "Property ['" + key + "'] not found in the current context");
        } else {
            return m.get(key);
        }
    }

    public Object getProperty(Object obj, Object key) {
        if (isMap(obj)) return ((Map) obj).get(key.toString());
        else {
            int index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
            return ((List) obj).get(index);
        }
    }

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

    public boolean isMap(Object obj) {
        return (obj instanceof Map);
    }

    public Collection<String> getPropertyKeys(Object obj) {
        if (isArray(obj)) {
            List l = (List) obj;
            List<String> keys = new ArrayList<String>(l.size());
            for (int i = 0; i < l.size(); i++) {
                keys.add(String.valueOf(i));
            }
            return keys;
        } else {
            return ((Map) obj).keySet();
        }
    }

    public int length(Object obj) {
        if (isArray(obj)) {
            return ((List) obj).size();
        }
        return getPropertyKeys(obj).size();
    }

    public Iterable<Object> toIterable(Object obj) {
        if (isArray(obj)) return ((Iterable) obj);
        else return ((Map) obj).values();
    }
}
