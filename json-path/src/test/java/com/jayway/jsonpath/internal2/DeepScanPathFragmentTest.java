package com.jayway.jsonpath.internal2;

import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeepScanPathFragmentTest {

    private static final Logger logger = LoggerFactory.getLogger(DeepScanPathFragmentTest.class);

    private JsonProvider jsonProvider = JsonProviderFactory.createProvider();

    private static final String DOCUMENT =
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
""";

    private static final String DOCUMENT2 =
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
 }
\s\s\s\s\s\s\s\
""";
}
