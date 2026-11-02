package com.jayway.jsonpath.filter;

import java.util.List;

public class RootFilter extends JsonPathFilterBase {

    @Override
    public List<Object> apply(List<Object> filter) {
        return filter;
    }
}
