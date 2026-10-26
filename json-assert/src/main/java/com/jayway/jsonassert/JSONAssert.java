package com.jayway.jsonassert;

import com.jayway.jsonassert.impl.JSONAsserterImpl;

import java.text.ParseException;

public class JSONAssert {

    public static JSONAsserter with(String json) throws ParseException {
        return JSONAsserterImpl.with(json);
    }

}
