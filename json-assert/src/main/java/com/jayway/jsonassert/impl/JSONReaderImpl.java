package com.jayway.jsonassert.impl;

import com.jayway.jsonassert.InvalidPathException;
import com.jayway.jsonassert.JSONReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.Validate.notNull;

public class JSONReaderImpl implements JSONReader {


    private static final JSONParser JSON_PARSER = new JSONParser();

    private Object root;
    private String currentPath;


    public static synchronized JSONReader parse(Reader reader) throws java.text.ParseException, IOException {
        try {
            return new JSONReaderImpl(JSON_PARSER.parse(reader));
        } catch (IOException e) {
            throw e;
        } catch (ParseException e) {
            throw new java.text.ParseException(e.getMessage(), e.getPosition());
        }
    }

    public static synchronized JSONReader parse(String jsonDoc) throws java.text.ParseException {
        try {
            return new JSONReaderImpl(JSON_PARSER.parse(jsonDoc));
        } catch (ParseException e) {
            throw new java.text.ParseException(e.getMessage(), e.getPosition());
        }
    }

    private JSONReaderImpl(Object root) {
        notNull(root, "root object can not be null");
        this.root = root;
    }

    public boolean hasJsonPath(String path) {
        boolean contains = true;
        try {
            get(path);
        } catch (InvalidPathException e) {
            contains = false;
        }
        return contains;
    }

    public boolean isNull(String path) {
        return (null == get(path));
    }

    public Object get(String path) {
        return getByPath(Object.class, path);
    }

    public String getString(String path) {
        return getByPath(String.class, path);
    }

    public Long getLong(String path) {
        return getByPath(Long.class, path);
    }

    public Double getDouble(String path) {
        return getByPath(Double.class, path);
    }

    public Boolean getBoolean(String path) {
        return getByPath(Boolean.class, path);
    }

    public <T> List<T> getList(String path) {
        return getByPath(List.class, path);
    }

    public Map getMap(String path) {
        return getByPath(Map.class, path);
    }


    //------------------------------------------------------------
    //
    // private methods
    //
    //------------------------------------------------------------


    private <T> T getByPath(Class<T> clazz, String stringPath) {
        currentPath = "";
        Object current = this.root;
        JSONPath path = new JSONPath(stringPath);

        while (path.hasMoreFragments()) {

            JSONPathFragment fragment = path.nextFragment();

            currentPath = fragment.appendToPath(currentPath);

            if (fragment.isArrayIndex()) {
                current = toArray(current).get(fragment.getArrayIndex());
            } else if (fragment.isArrayWildcard()) {
                current = getContainerValue(current, path.nextFragment());
            } else {
                current = getContainerValue(current, fragment);
            }
        }
        return clazz.cast(current);
    }

    private JSONArray toArray(Object array) {
        return (JSONArray) array;
    }

    private JSONObject toDocument(Object document) {
        return (JSONObject) document;
    }

    private Object getContainerValue(Object container, JSONPathFragment fragment) {
        Object result;

        if (container instanceof JSONArray) {
            List list = new LinkedList();
            for (Object doc : toArray(container)) {
                list.add(getContainerValue(doc, fragment));
            }
            result = list;

        } else if (container instanceof JSONObject) {
            JSONObject document = toDocument(container);

            if (!document.containsKey(fragment.value())) {
                throw new InvalidPathException("Invalid path element: " + currentPath + " <==");
            }

            result = document.get(fragment.value());
        } else {
            throw new InvalidPathException("Invalid path element: " + currentPath + " <==");
        }
        return result;
    }
}
