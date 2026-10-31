package com.jayway.jsonassert.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PathFragment {

    public static final PathFragment WILDCARD_FRAGMENT = new PathFragment("*", false);

    private static final Pattern ARRAY_POSITION_PATTER = Pattern.compile("\\[(\\d*)\\]");

    private static final Pattern GROOVY_POSITION_PATTER = Pattern.compile("get\\((\\d*)\\)");

    private static final Pattern ARRAY_WILDCARD_PATTER = Pattern.compile("\\[\\*\\]");

    private static final Pattern GROOVY_WILDCARD_PATTER = Pattern.compile("get\\(\\*\\)");

    private final String value;

    private final boolean leaf;

    PathFragment(String value, boolean leaf) {
        this.value = value;
        this.leaf = leaf;
    }

    @Override
    public String toString() {
        return "JSONPathFragment{" +
                "value='" + value + '\'' +
                '}';
    }

    String value() {
        return value;
    }

    String appendToPath(String path){
        StringBuilder builder = new StringBuilder(path);

        if(ARRAY_POSITION_PATTER.matcher(value).matches()){
            builder.append("[").append(getArrayIndex()).append("]");
        }
        else if(GROOVY_POSITION_PATTER.matcher(value).matches()){
            builder.append(path.isEmpty()?"":".").append("get(").append(getArrayIndex()).append(")");
        }
        else if(ARRAY_WILDCARD_PATTER.matcher(value).matches()){
            builder.append("[*]");
        }
        else if(GROOVY_WILDCARD_PATTER.matcher(value).matches()){
            builder.append(path.isEmpty()?"":".").append("get(*)");
        }
        else {
           builder.append(path.isEmpty()?"":".").append(value);
        }
        return builder.toString();
    }

    boolean isLeaf(){
        return leaf;
    }

    boolean isArrayIndex() {
        return ARRAY_POSITION_PATTER.matcher(value).matches() || GROOVY_POSITION_PATTER.matcher(value).matches();
    }

    boolean isArrayWildcard() {
        return ARRAY_WILDCARD_PATTER.matcher(value).matches() || GROOVY_WILDCARD_PATTER.matcher(value).matches();
    }

    boolean isWildcard(){
        return "*".endsWith(value());
    }

    int getArrayIndex() {
        Matcher matcher = ARRAY_POSITION_PATTER.matcher(value);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        matcher = GROOVY_POSITION_PATTER.matcher(value);
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new UnsupportedOperationException("not an array index fragment");
    }
}
