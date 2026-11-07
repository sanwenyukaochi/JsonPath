package com.jayway.jsonpath.internal.function;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;

public class Issue273 {
    @Test
    public void testGetPropertyFromArray() {
        String json =
"""
[
   [
      {
         "category" : "reference",
         "author" : "Nigel Rees",
         "title" : "Sayings of the Century",
         "price" : 8.95
      },
      {
         "category" : "fiction",
         "author" : "Evelyn Waugh",
         "title" : "Sword of Honour",
         "price" : 12.99
      },
      {
         "category" : "fiction",
         "author" : "Herman Melville",
         "title" : "Moby Dick",
         "isbn" : "0-553-21311-3",
         "price" : 8.99
      },
      {
         "category" : "fiction",
         "author" : "J. R. R. Tolkien",
         "title" : "The Lord of the Rings",
         "isbn" : "0-395-19395-8",
         "price" : 22.99
      }
   ],
   {
      "color" : "red",
      "price" : 19.95
   }
]
""";

        JSONArray arr = JsonPath.read(json, "$..[2].author");
        assertEquals(arr.get(0), "Herman Melville");
    }

    @Test
    public void testGetPropertyFromObject() {
        String json =
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
                "category": "fiction",
                "author": "J. R. R. Tolkien",
                "title": "The Lord of the Rings",
                "isbn": "0-395-19395-8",
                "price": 22.99
            }
        ],
        "bicycle": {
            "color": "red",
            "price": 19.95
        }
    },
    "expensive": 10
}
\s\s\s\s\s\s\s\s\s\s\s\s\s\s\s\s\
""";
        JSONArray arr = JsonPath.read(json, "$..[2].author");
        assertEquals(arr.get(0), "Herman Melville");
    }
}
