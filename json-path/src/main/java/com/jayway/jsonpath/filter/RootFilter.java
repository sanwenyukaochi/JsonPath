package com.jayway.jsonpath.filter;

public class RootFilter extends JsonPathFilterBase {

    @Override
    public FilterOutput apply(FilterOutput root) {
        return root;
    }
}
