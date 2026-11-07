package com.jayway.jsonpath.functions;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;

import net.minidev.json.JSONArray;

import org.junit.jupiter.api.Test;

public class JSONEntityFunctionTest extends BaseFunctionTest {

    private static final String BATCH_JSON =
"""
{
  "batches": {
    "minBatchSize": 10,
    "results": [
      {
        "productId": 23,
        "values": [
          2,
          45,
          34,
          23,
          3,
          5,
          4,
          3,
          2,
          1,
        ]
      },
      {
        "productId": 23,
        "values": [
          52,
          3,
          12,
          11,
          18,
          22,
          1
        ]
      }
    ]
  }
}\
""";

    private Configuration conf = Configurations.JSON_SMART_CONFIGURATION;

    @Test
    public void testLengthOfTextArray() {

        verifyFunction(conf, "$['text'].length()", TEXT_SERIES, 6);
    }

    @Test
    public void testLengthOfNumberArray() {

        verifyFunction(conf, "$.numbers.length()", NUMBER_SERIES, 10);
    }

    @Test
    public void testLengthOfStructure() {
        verifyFunction(conf, "$.batches.length()", BATCH_JSON, 2);
    }

    @Test
    public void testPredicateWithFunctionCallSingleMatch() {
        String path =
                "$.batches.results[?(@.values.length() >= $.batches.minBatchSize)].values.avg()";

        JSONArray values = new JSONArray();
        values.add(12.2d);
        verifyFunction(conf, path, BATCH_JSON, values);
    }

    @Test
    public void testPredicateWithFunctionCallTwoMatches() {
        String path = "$.batches.results[?(@.values.length() >= 3)].values.avg()";

        JSONArray values = new JSONArray();
        values.add(12.2d);
        values.add(17d);
        verifyFunction(conf, path, BATCH_JSON, values);
    }
}
