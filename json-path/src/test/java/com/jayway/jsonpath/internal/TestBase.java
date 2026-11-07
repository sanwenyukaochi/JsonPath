package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;

public class TestBase {

    public static final Object ARRAY =
            JsonProviderFactory.createProvider()
                    .parse(
"""
[{
   "foo" : "foo-val-0"
},{
   "foo" : "foo-val-1"
},{
   "foo" : "foo-val-2"
},{
   "foo" : "foo-val-3"
},{
   "foo" : "foo-val-4"
},{
   "foo" : "foo-val-5"
},{
   "foo" : "foo-val-6"
}]\
""");

    public static final Object DOC =
            JsonProviderFactory.createProvider()
                    .parse(
"""
{ "store": {
    "book": [\s
      { "category": "reference",
        "author": "Nigel Rees",
        "title": "Sayings of the Century",
        "display-price": 8.95
      },
      { "category": "fiction",
        "author": "Evelyn Waugh",
        "title": "Sword of Honour",
        "display-price": 12.99
      },
      { "category": "fiction",
        "author": "Herman Melville",
        "title": "Moby Dick",
        "isbn": "0-553-21311-3",
        "display-price": 8.99
      },
      { "category": "fiction",
        "author": "J. R. R. Tolkien",
        "title": "The Lord of the Rings",
        "isbn": "0-395-19395-8",
        "display-price": 22.99
      }
    ],
    "bicycle": {
      "color": "red",
      "display-price": 19.95,
      "foo:bar": "fooBar",
      "dot.notation": "new",
      "dash-notation": "dashes"
    }
  }
}\
""");

    public static final Configuration CONF = Configuration.defaultConfiguration();
}
