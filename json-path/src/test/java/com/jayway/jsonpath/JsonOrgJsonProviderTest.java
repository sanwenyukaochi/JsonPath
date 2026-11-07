package com.jayway.jsonpath;

import static com.jayway.jsonpath.JsonPath.using;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class JsonOrgJsonProviderTest extends BaseTest {

    @Test
    public void an_object_can_be_read() {

        JSONObject book =
                using(JSON_ORG_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[0]");

        System.out.println(book);
    }

    @Test
    public void a_property_can_be_read() {

        String category =
                using(JSON_ORG_CONFIGURATION).parse(JSON_DOCUMENT).read("$.store.book[0].category");

        System.out.println(category);
    }

    @Test
    public void a_filter_can_be_applied() {

        JSONArray fictionBooks =
                using(JSON_ORG_CONFIGURATION)
                        .parse(JSON_DOCUMENT)
                        .read("$.store.book[?(@.category == 'fiction')]");

        System.out.println(fictionBooks);
    }

    @Test
    public void result_can_be_mapped_to_object() {

        Object result =
                using(JSON_ORG_CONFIGURATION)
                        .parse(JSON_DOCUMENT)
                        .read("$.store.book", Object.class);

        System.out.println(result);
    }
}
