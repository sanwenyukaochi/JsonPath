package com.jayway.jsonpath;

import org.junit.jupiter.api.Test;

public class DocumentationPageTests {

    public static final String JSON =
            """
            {
                "store": {
                    "book": [
                        {
                            "category": "reference",
                            "author": "Nigel Rees",
                            "title": "Sayings of the Century",
                            "price": 8.95
                        },
                        {
                            "category": "fiction",
                            "author": "Evelyn Waugh",
                            "title": "Sword of Honour",
                            "price": 12.99
                        },
                        {
                            "category": "fiction",
                            "author": "Herman Melville",
                            "title": "Moby Dick",
                            "isbn": "0-553-21311-3",
                            "price": 8.99
                        },
                        {
                            "category": null,
                            "author": "J. R. R. Tolkien",
                            "title": "The Lord of the Rings",
                            "isbn": "0-395-19395-8",
                            "price": 22.99,
                            "edition": {
                               "release-date": "2013-01-22"
                            }
                        }
                    ],
                    "bicycle": {
                        "color": "red",
                        "price": 19.95
                     }
                }
            }\
            """;

    @Test
    public void test_1() {
        System.out.println((Object) JsonPath.read(JSON, "$.store.bicycle"));
    }

    @Test
    public void test_2() {
        System.out.println((Object) JsonPath.read(JSON, "$.store.book[0]"));
    }

    @Test
    public void test_3() {
        System.out.println((Object) JsonPath.read(JSON, "$.store.book[*].author"));
    }

    @Test
    public void test_4() {
        System.out.println((Object) JsonPath.read(JSON, "$.store.book[-2:].author"));
    }

    @Test
    public void test_5() {
        System.out.println((Object) JsonPath.read(JSON, "$.store.book[1:3].author"));
    }

    @Test
    public void test_6() {
        System.out.println((Object) JsonPath.read(JSON, "$.store.book[0]['author', 'category']"));
    }

    @Test
    public void test_7() {
        System.out.println((Object) JsonPath.read(JSON, "$..price"));
    }
}
