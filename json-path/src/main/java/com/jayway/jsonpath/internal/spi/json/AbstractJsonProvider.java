package com.jayway.jsonpath.internal.spi.json;

import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.ArrayList;
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

    public boolean isMap(Object obj) {
        return (obj instanceof Map);
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    public Iterable<Object> toIterable(Object obj) {
        if (isArray(obj)) return ((Iterable) obj);
        else return ((Map) obj).values();
    }
}
