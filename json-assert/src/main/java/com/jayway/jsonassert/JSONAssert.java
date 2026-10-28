package com.jayway.jsonassert;

import com.jayway.jsonassert.impl.JsonAsserterImpl;
import com.jayway.jsonassert.impl.JsonReaderImpl;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

public class JsonAssert {

    public static JsonPath parse(String jsonDoc) throws ParseException {
        return JsonReaderImpl.parse(jsonDoc);
    }

    public static JsonPath parse(Reader reader) throws ParseException, IOException {
        return parse(reader, false);
    }

    public static JsonPath parse(Reader reader, boolean closeReader) throws ParseException, IOException {
        JsonPath jsonReader = null;
        try {
            jsonReader = JsonReaderImpl.parse(reader);
        } finally {
            if(closeReader){
                try {
                    reader.close();
                } catch (IOException ignore) {}
            }
        }
        return jsonReader;
    }

    public static JsonAsserter with(String json) throws ParseException {
        return new JsonAsserterImpl(JsonReaderImpl.parse(json));
    }

    public static JsonAsserter with(Reader reader) throws ParseException, IOException {
        return new JsonAsserterImpl(JsonReaderImpl.parse(reader));
    }

}
