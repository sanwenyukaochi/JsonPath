package com.jayway.jsonpath.old;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jayway.jsonpath.JsonPath;
import java.util.List;
import org.junit.jupiter.api.Test;

public class HelpTest {

    public static final String JSON =
            """
            {
               "destination_addresses" : [
                  "Rua do Dom António José Cordeiro 48, 3800-012 Aveiro, Portugal",
                  "Rua do Dom António José Cordeiro 48, 3800-012 Aveiro, Portugal"
               ],
               "origin_addresses" : [ "N109, 3800, Portugal" ],
               "rows" : [
                  {
                     "elements" : [
                        {
                           "distance" : {
                              "text" : "0.4 km",
                              "value" : 427
                           },
                           "duration" : {
                              "text" : "1 min",
                              "value" : 58
                           },
                           "status" : "OK"
                        },
                        {
                           "distance" : {
                              "text" : "5.0 km",
                              "value" : 427
                           },
                           "duration" : {
                              "text" : "1 min",
                              "value" : 58
                           },
                           "status" : "OK"
                        }
                     ]
                  }
               ],
               "status" : "OK"
            }\
            """;

    private static final String JSON2 =
            """
            {
                "error": null,
                "contents": [
                    {
                        "groupType": "series",
                         "instanceId": "grp://15",
                         "id": "prg://16",
                        "type": "group",
                        "media": [
                            {
                                "classification": "urn:1.2.3",
                                "uri": "http://yahoo.com/1.png",
                                "mimeType": "application/octet-stream"
                            }
                        ]
                    },
                    {
                        "groupType": "series",
                         "instanceId": "grp://15",
                         "extra": 1,
                         "id": "prg://16",
                        "type": "group",
                        "media": [
                            {
                                "classification": "urn:1.2.3",
                                "uri": "http://yahoo.com/1.png",
                                "mimeType": "application/octet-stream"
                            }
                        ]
                    }
                ],
                "header": {
                    "total": 1
                }
            }\
            """;

    @Test
    public void sample_one_a() {
        System.out.println((Object) JsonPath.read(JSON2, "$.['error', 'header']"));
        System.out.println(
                (Object) JsonPath.read(JSON2, "$.contents[*].['groupType', 'type', 'extra']"));
    }

    @Test
    public void sample_one_b() {
        System.out.println((Object) JsonPath.read(JSON2, "$.['error', 'header']"));
        System.out.println((Object) JsonPath.read(JSON2, "$.contents[*].['groupType', 'type']"));
    }

    @Test
    public void sample_one() {
        List<String> addresses = JsonPath.read(JSON, "$.destination_addresses[*]");
        assertThat(
                addresses,
                hasItems("Rua do Dom António José Cordeiro 48, 3800-012 Aveiro, Portugal"));
    }

    @Test
    public void sample_two() {
        String text = JsonPath.read(JSON, "$.rows[0].elements[1].distance.text");
        assertEquals("5.0 km", text);
    }

    @Test
    public void sample_two_b() {
        String text = JsonPath.read(JSON, "$.rows[0].elements[1].distance.text");
        assertEquals("5.0 km", text);
    }

    @Test
    public void sample_three() {
        List<String> allDistanceTexts = JsonPath.read(JSON, "$.rows[0].elements[*].distance.text");
        assertThat(allDistanceTexts, hasItems("0.4 km", "5.0 km"));
    }
}
