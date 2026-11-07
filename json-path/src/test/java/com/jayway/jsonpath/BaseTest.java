package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.token.PredicateContextImpl;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import java.util.HashMap;

public class BaseTest {
    public static final Configuration GSON_CONFIGURATION =
            Configuration.builder()
                    .mappingProvider(new GsonMappingProvider())
                    .jsonProvider(new GsonJsonProvider())
                    .build();

    public static final Configuration JACKSON_CONFIGURATION =
            Configuration.builder()
                    .mappingProvider(new JacksonMappingProvider())
                    .jsonProvider(new JacksonJsonProvider())
                    .build();

    public static final Configuration JACKSON_JSON_NODE_CONFIGURATION =
            Configuration.builder()
                    .mappingProvider(new JacksonMappingProvider())
                    .jsonProvider(new JacksonJsonNodeJsonProvider())
                    .build();

    public static final Configuration JSON_SMART_CONFIGURATION =
            Configuration.defaultConfiguration();

    public static final String JSON_BOOK_DOCUMENT =
"""
{    "category" : "reference",
   "author" : "Nigel Rees",
   "title" : "Sayings of the Century",
   "display-price" : 8.95
}\
""";

    public static final String JSON_DOCUMENT =
            """
            {
                "string-property": "string-value",
                "int-max-property": 2147483647,
                "long-max-property": 9223372036854775807,
                "boolean-property": true,
                "null-property": null,
                "int-small-property": 1,
                "max-price": 10,
                "store": {
                    "book": [
                        {
                            "category": "reference",
                            "author": "Nigel Rees",
                            "title": "Sayings of the Century",
                            "display-price": 8.95
                        },
                        {
                            "category": "fiction",
                            "author": "Evelyn Waugh",
                            "title": "Sword of Honour",
                            "display-price": 12.99
                        },
                        {
                            "category": "fiction",
                            "author": "Herman Melville",
                            "title": "Moby Dick",
                            "isbn": "0-553-21311-3",
                            "display-price": 8.99
                        },
                        {
                            "category": "fiction",
                            "author": "J. R. R. Tolkien",
                            "title": "The Lord of the Rings",
                            "isbn": "0-395-19395-8",
                            "display-price": 22.99
                        }
                    ],
                    "bicycle": {
                        "foo": "baz",
                        "color": "red",
                        "display-price": 19.95,
                        "foo:bar": "fooBar",
                        "dot.notation": "new",
                        "dash-notation": "dashes"
                    }
                },
                "foo": "bar",
                "@id": "ID"
            }
            """;

    public Predicate.PredicateContext createPredicateContext(final Object check) {

        return new PredicateContextImpl(
                check, check, Configuration.defaultConfiguration(), new HashMap<Path, Object>());
    }
}
