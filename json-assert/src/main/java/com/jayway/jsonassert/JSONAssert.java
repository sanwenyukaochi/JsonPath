package com.jayway.jsonassert;

import com.jayway.jsonassert.impl.JSONAsserterImpl;
import com.jayway.jsonassert.impl.JSONReaderImpl;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

public class JSONAssert {

    public static JSONReader parse(String jsonDoc) throws ParseException {
        return JSONReaderImpl.parse(jsonDoc);
    }

    public static JSONReader parse(Reader reader) throws ParseException, IOException {
        return parse(reader, false);
    }

    public static JSONReader parse(Reader reader, boolean closeReader) throws ParseException, IOException {
        JSONReader jsonReader = null;
        try {
            jsonReader = JSONReaderImpl.parse(reader);
        } finally {
            if(closeReader){
                try {
                    reader.close();
                } catch (IOException ignore) {}
            }
        }
        return jsonReader;
    }

    public static JSONAsserter with(String json) throws ParseException {
        return new JSONAsserterImpl(JSONReaderImpl.parse(json));
    }

    public static JSONAsserter with(Reader reader) throws ParseException, IOException {
        return new JSONAsserterImpl(JSONReaderImpl.parse(reader));
    }

}
