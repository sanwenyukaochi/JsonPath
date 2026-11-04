package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.JsonUtil;
import java.util.List;
import org.json.simple.JSONArray;

public class PropertyFilter extends JsonPathFilterBase {

    private static final String WILDCARD = "*";

    private final String pathFragment;

    public PropertyFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public List<Object> apply(List<Object> filter) {

        List<Object> result = new JSONArray();

        if (WILDCARD.equals(pathFragment)) {
            for (Object current : filter) {
                for (Object value : JsonUtil.toMap(current).values()) {
                    result.add(value);
                }
            }
        } else {
            for (Object current : filter) {
                if (JsonUtil.toMap(current).containsKey(pathFragment)) {
                    result.add(JsonUtil.toMap(current).get(pathFragment));
                }
            }
        }

        return result;
    }
}
