package com.jayway.jsonpath.filter;

import com.jayway.jsonpath.PathItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.List;

public abstract class JsonPathFilterBase {




    public abstract List<Object> apply(List<Object> filter);


}
