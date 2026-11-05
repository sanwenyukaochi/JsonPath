package com.jayway.jsonassert;

import com.jayway.jsonassert.impl.JsonAsserterImpl;
import com.jayway.jsonassert.impl.matcher.CollectionMatcher;
import com.jayway.jsonassert.impl.matcher.IsCollectionWithSize;
import com.jayway.jsonassert.impl.matcher.IsEmptyCollection;
import com.jayway.jsonassert.impl.matcher.IsMapContainingKey;
import com.jayway.jsonassert.impl.matcher.IsMapContainingValue;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;
import org.hamcrest.Matcher;
import org.json.simple.parser.JSONParser;

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

    public static CollectionMatcher collectionWithSize(Matcher<? super Integer> sizeMatcher) {
        return new IsCollectionWithSize(sizeMatcher);
    }

    public static Matcher<Map<String, ?>> mapContainingKey(Matcher<String> keyMatcher) {
        return new IsMapContainingKey(keyMatcher);
    }

    public static <V> Matcher<? super Map<?, V>> mapContainingValue(
            Matcher<? super V> valueMatcher) {
        return new IsMapContainingValue<V>(valueMatcher);
    }

    public static Matcher<Collection<Object>> emptyCollection() {
        return new IsEmptyCollection<Object>();
    }
}
