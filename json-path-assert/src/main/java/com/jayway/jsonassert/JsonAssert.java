package com.jayway.jsonassert;


import com.jayway.jsonassert.impl.JsonAsserterImpl;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;

public class JsonAssert {

    private static final JSONParser JSON_PARSER = new JSONParser();

    public static JsonAsserter with(String json) throws ParseException {
        try {
            return new JsonAsserterImpl(JSON_PARSER.parse(json));
        } catch (org.json.simple.parser.ParseException e) {
            throw new ParseException(json, e.getPosition());
        }
    }

    public static JsonAsserter with(Reader reader) throws ParseException, IOException {
        try {
            return new JsonAsserterImpl(JSON_PARSER.parse(reader));
        } catch (org.json.simple.parser.ParseException e) {
            throw new ParseException(e.toString(), e.getPosition());
        } finally {
            reader.close();
        }
    }

    public static JsonAsserter with(InputStream is) throws ParseException, IOException {
        Reader reader = new InputStreamReader(is);
        return with(reader);
    }

}
