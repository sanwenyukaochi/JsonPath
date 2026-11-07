package com.jayway.jsonpath.old.internal;

import static com.jayway.jsonpath.JsonPath.read;
import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.PathCompiler;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ScanPathTokenTest {

    public static final Object DOCUMENT =
            JsonProviderFactory.createProvider()
                    .parse(
"""
{
 "store":{
  "book":[
   {
    "category":"reference",
    "author":"Nigel Rees",
    "title":"Sayings of the Century",
    "price":8.95,
    "address":{         "street":"fleet street",
        "city":"London"
      }
   },
   {
    "category":"fiction",
    "author":"Evelyn Waugh",
    "title":"Sword of Honour",
    "price":12.9,
    "address":{\s
        "street":"Baker street",
        "city":"London"
      }
   },
   {
    "category":"fiction",
    "author":"J. R. R. Tolkien",
    "title":"The Lord of the Rings",
    "isbn":"0-395-19395-8",
    "price":22.99,    "address":{         "street":"Svea gatan",
        "city":"Stockholm"
      }
   }
  ],
  "bicycle":{
   "color":"red",
   "price":19.95,   "address":{         "street":"Söder gatan",
        "city":"Stockholm"
      },
   "items": [["A","B","C"],1,2,3,4,5]
  }
 }
}\
""");

    public static final Object DOCUMENT2 =
            JsonProviderFactory.createProvider()
                    .parse(
"""
{
     "firstName": "John",
     "lastName" : "doe",
     "age"      : 26,
     "address"  :
     {
         "streetAddress": "naist street",
         "city"         : "Nara",
         "postalCode"   : "630-0192"
     },
     "phoneNumbers":
     [
         {
           "type"  : "iPhone",
           "number": "0123-4567-8888"
         },
         {
           "type"  : "home",
           "number": "0123-4567-8910"
         }
     ]
 }\
""");

    @Test
    public void a_root_scan() {

        Object o = JsonPath.read(DOCUMENT, "$..");
    }

    @Test
    public void a_document_can_be_scanned_for_property() {

        List<String> result = read(DOCUMENT, "$..author");

        assertThat(result).containsOnly("Nigel Rees", "Evelyn Waugh", "J. R. R. Tolkien");
    }

    @Test
    public void a_document_can_be_scanned_for_property_path() {

        List<String> result = read(DOCUMENT, "$..address.street");

        assertThat(result)
                .containsOnly("fleet street", "Baker street", "Svea gatan", "Söder gatan");
    }

    @Test
    public void a_document_can_be_scanned_for_wildcard() {

        List<String> result =
                PathCompiler.compile("$..[*]")
                        .evaluate(DOCUMENT, Configuration.defaultConfiguration())
                        .getPathList();

        assertThat(result)
                .containsOnly(
                        "$['store']",
                        "$['store']['bicycle']",
                        "$['store']['book']",
                        "$['store']['bicycle']['address']",
                        "$['store']['bicycle']['color']",
                        "$['store']['bicycle']['price']",
                        "$['store']['bicycle']['items']",
                        "$['store']['bicycle']['address']['city']",
                        "$['store']['bicycle']['address']['street']",
                        "$['store']['bicycle']['items'][0]",
                        "$['store']['bicycle']['items'][1]",
                        "$['store']['bicycle']['items'][2]",
                        "$['store']['bicycle']['items'][3]",
                        "$['store']['bicycle']['items'][4]",
                        "$['store']['bicycle']['items'][5]",
                        "$['store']['bicycle']['items'][0][0]",
                        "$['store']['bicycle']['items'][0][1]",
                        "$['store']['bicycle']['items'][0][2]",
                        "$['store']['book'][0]",
                        "$['store']['book'][1]",
                        "$['store']['book'][2]",
                        "$['store']['book'][0]['address']",
                        "$['store']['book'][0]['author']",
                        "$['store']['book'][0]['price']",
                        "$['store']['book'][0]['category']",
                        "$['store']['book'][0]['title']",
                        "$['store']['book'][0]['address']['city']",
                        "$['store']['book'][0]['address']['street']",
                        "$['store']['book'][1]['address']",
                        "$['store']['book'][1]['author']",
                        "$['store']['book'][1]['price']",
                        "$['store']['book'][1]['category']",
                        "$['store']['book'][1]['title']",
                        "$['store']['book'][1]['address']['city']",
                        "$['store']['book'][1]['address']['street']",
                        "$['store']['book'][2]['address']",
                        "$['store']['book'][2]['author']",
                        "$['store']['book'][2]['price']",
                        "$['store']['book'][2]['isbn']",
                        "$['store']['book'][2]['category']",
                        "$['store']['book'][2]['title']",
                        "$['store']['book'][2]['address']['city']",
                        "$['store']['book'][2]['address']['street']");
    }

    @Test
    public void a_document_can_be_scanned_for_wildcard2() {

        List<String> result =
                PathCompiler.compile("$.store.book[0]..*")
                        .evaluate(DOCUMENT, Configuration.defaultConfiguration())
                        .getPathList();

        assertThat(result)
                .containsOnly(
                        "$['store']['book'][0]['address']",
                        "$['store']['book'][0]['author']",
                        "$['store']['book'][0]['price']",
                        "$['store']['book'][0]['category']",
                        "$['store']['book'][0]['title']",
                        "$['store']['book'][0]['address']['city']",
                        "$['store']['book'][0]['address']['street']");
    }

    @Test
    public void a_document_can_be_scanned_for_wildcard3() {

        List<String> result =
                PathCompiler.compile("$.phoneNumbers[0]..*")
                        .evaluate(DOCUMENT2, Configuration.defaultConfiguration())
                        .getPathList();

        assertThat(result)
                .containsOnly("$['phoneNumbers'][0]['number']", "$['phoneNumbers'][0]['type']");
    }

    @Test
    public void a_document_can_be_scanned_for_predicate_match() {

        List<String> result =
                PathCompiler.compile("$..[?(@.address.city == 'Stockholm')]")
                        .evaluate(DOCUMENT, Configuration.defaultConfiguration())
                        .getPathList();

        assertThat(result).containsOnly("$['store']['bicycle']", "$['store']['book'][2]");
    }

    @Test
    public void a_document_can_be_scanned_for_existence() {

        List<String> result =
                PathCompiler.compile("$..[?(@.isbn)]")
                        .evaluate(DOCUMENT, Configuration.defaultConfiguration())
                        .getPathList();

        assertThat(result).containsOnly("$['store']['book'][2]");
    }

    @Test
    public void a_document_can_be_scanned_for_array_indexes() {

        List<String> result =
                PathCompiler.compile("$..[(@.length - 1)]")
                        .evaluate(DOCUMENT, Configuration.defaultConfiguration())
                        .getPathList();

        assertThat(result)
                .containsOnly(
                        "$['store']['bicycle']['items'][5]",
                        "$['store']['bicycle']['items'][0][2]",
                        "$['store']['book'][2]");
    }
}
