package com.jayway.jsonpath.internal2;

import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterPredicatePathFragmentTest {

    private static final Logger logger =
            LoggerFactory.getLogger(FilterPredicatePathFragmentTest.class);

    private JsonProvider jsonProvider = JsonProviderFactory.createProvider();

    private String SIMPLE_ARRAY =
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
""";

    private String SIMPLE_ARRAY_2 =
"""
[{
   "foo" : "foo-val-0",
   "int" : 0
,   "decimal" : 0.0
},{
   "foo" : "foo-val-1",
   "int" : 1,
   "decimal" : 0.1
},{
   "foo" : "foo-val-2",
   "int" : 2,
   "decimal" : 0.2
},{
   "foo" : "foo-val-3",
   "int" : 3,
   "decimal" : 0.3
},{
   "foo" : "foo-val-4",
   "int" : 4,
   "decimal" : 0.4
},{
   "foo" : "foo-val-5",
   "int" : 5,
   "decimal" : 0.5
},{
   "foo" : "foo-val-6",
   "int" : 6,
   "decimal" : 0.6
},{
   "foo" : "foo-val-7",
   "int" : 7,
   "decimal" : 0.7,
   "bool" : true
}]\
""";
}
