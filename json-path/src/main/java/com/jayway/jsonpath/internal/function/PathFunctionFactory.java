package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.internal.function.numeric.Average;
import com.jayway.jsonpath.internal.function.numeric.Max;
import com.jayway.jsonpath.internal.function.numeric.Min;
import com.jayway.jsonpath.internal.function.numeric.StandardDeviation;
import com.jayway.jsonpath.internal.function.numeric.Sum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PathFunctionFactory {

    public static final Map<String, Class> FUNCTIONS;

    static {
        Map<String, Class> map = new HashMap<String, Class>();

        map.put("avg", Average.class);
        map.put("stddev", StandardDeviation.class);
        map.put("sum", Sum.class);
        map.put("min", Min.class);
        map.put("max", Max.class);

        map.put("length", Length.class);

        FUNCTIONS = Collections.unmodifiableMap(map);
    }

    public static PathFunction newFunction(String name) throws InvalidPathException {
        PathFunction result = new PassthruPathFunction();

        if (null != name
                && FUNCTIONS.containsKey(name)
                && PathFunction.class.isAssignableFrom(FUNCTIONS.get(name))) {
            try {
                result = (PathFunction) FUNCTIONS.get(name).newInstance();
            } catch (InstantiationException e) {
                throw new InvalidPathException(
                        "Function of name: " + name + " cannot be created", e);
            } catch (IllegalAccessException e) {
                throw new InvalidPathException(
                        "Function of name: " + name + " cannot be created", e);
            }
        }
        return result;
    }
}
