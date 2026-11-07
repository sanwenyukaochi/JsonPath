package com.jayway.jsonpath.internal2;

import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;

public class PathWalkerTest {

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
    "price":8.95
   },
   {
    "category":"fiction",
    "author":"Evelyn Waugh",
    "title":"Sword of Honour",
    "price":12.99
   },
   {
    "category":"fiction",
    "author":"J. R. R. Tolkien",
    "title":"The Lord of the Rings",
    "isbn":"0-395-19395-8",
    "price":22.99
   }
  ],
  "bicycle":{
   "color":"red",
   "price":19.95
  }
 }
}\
""";
}
