package com.jayway.jsonpath.reader.filter;

public class PassThrewFilter extends Filter {

    public PassThrewFilter(String condition) {
        super(condition);
    }

    public Object filter(Object obj) {
        return obj;
    }
}
