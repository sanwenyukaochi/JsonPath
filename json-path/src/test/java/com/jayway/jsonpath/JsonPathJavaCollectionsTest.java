package com.jayway.jsonpath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class JsonPathJavaCollectionsTest {

    private static final List<Map<String, Object>> BOOKS =
            new ArrayList<>(
                    List.of(
                            new HashMap<>(Map.of("author", "Nigel Rees", "price", 8)),
                            new HashMap<>(
                                    Map.of("author", "Evelyn Waugh", "price", 12, "isbn", "123"))));

    private static final Map<String, Object> DOCUMENT =
            new HashMap<>(
                    Map.of(
                            "store",
                            new HashMap<>(
                                    Map.of(
                                            "book",
                                            BOOKS,
                                            "bicycle",
                                            new HashMap<>(Map.of("color", "red"))))));

    @ParameterizedTest(name = "{0}")
    @MethodSource("paths")
    public void reads_plain_java_maps_and_lists(String path, List<?> expected) {
        assertEquals(expected, JsonPath.read(DOCUMENT, path));
    }

    static Stream<Arguments> paths() {
        return Stream.of(
                Arguments.of("$.store.book[0].author", List.of("Nigel Rees")),
                Arguments.of("$.store.book[*].author", List.of("Nigel Rees", "Evelyn Waugh")),
                Arguments.of("$.store.book[0,1].author", List.of("Nigel Rees", "Evelyn Waugh")),
                Arguments.of("$.store.book[:2].author", List.of("Nigel Rees", "Evelyn Waugh")),
                Arguments.of("$.store.book[-1:].author", List.of("Evelyn Waugh")),
                Arguments.of("$.store.book[(@.length-1)].author", List.of("Evelyn Waugh")),
                Arguments.of("$.store.book[?(@.isbn)].author", List.of("Evelyn Waugh")),
                Arguments.of("$.store.book[?(@.price < 10)].author", List.of("Nigel Rees")),
                Arguments.of("$..book[?(@.author = 'Nigel Rees')].author", List.of("Nigel Rees")),
                Arguments.of("$.store.bicycle.*", List.of("red")),
                Arguments.of("$..author", List.of("Nigel Rees", "Evelyn Waugh")));
    }

    @Test
    public void reads_a_plain_java_list_at_the_root() {
        assertEquals(List.of("Nigel Rees", "Evelyn Waugh"), JsonPath.read(BOOKS, "$.[*].author"));
        assertEquals(List.of("Nigel Rees", "Evelyn Waugh"), JsonPath.read(BOOKS, "$..author"));
        assertEquals("Evelyn Waugh", JsonPath.readOne(BOOKS, "$.[1].author"));
    }
}
