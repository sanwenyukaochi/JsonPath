package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.JsonUtil;
import java.util.List;
import java.util.regex.Pattern;
import net.minidev.json.JSONArray;

public class PropertyFilter extends JsonPathFilterBase {

    public static final Pattern PATTERN = Pattern.compile("(.*)|\\['(.*?)'\\]");

    private final String pathFragment;

    public PropertyFilter(String pathFragment) {
        this.pathFragment = pathFragment;
    }

    @Override
    public FilterOutput apply(FilterOutput filter) {

        List<Object> result = new JSONArray();

        if (filter.isList()) {
            for (Object current : filter.getResultAsList()) {
                if (JsonUtil.toMap(current).containsKey(pathFragment)) {

                    Object o = JsonUtil.toMap(current).get(pathFragment);
                    if (JsonUtil.isList(o)) {
                        result.addAll(JsonUtil.toList(o));

                    } else {
                        result.add(JsonUtil.toMap(current).get(pathFragment));
                    }
                }
            }
            return new FilterOutput(result);
        } else {
            Object mapValue = null;
            if (JsonUtil.toMap(filter.getResult()).containsKey(pathFragment)) {
                mapValue = JsonUtil.toMap(filter.getResult()).get(pathFragment);
            }
            return new FilterOutput(mapValue);
        }
    }
}
