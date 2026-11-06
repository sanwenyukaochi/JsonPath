package com.jayway.jsonpath.reader.filter;

import com.jayway.jsonpath.spi.JsonProvider;

public class PassThrewFilter extends Filter {

    public PassThrewFilter(String condition) {
        super(condition);
    }

    public Object filter(Object obj, JsonProvider jsonProvider) {
        return obj;
    }
}
