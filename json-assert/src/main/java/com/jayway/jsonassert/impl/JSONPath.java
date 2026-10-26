package com.jayway.jsonassert.impl;

import java.util.LinkedList;
import java.util.Queue;

class JSONPath {

    private final Queue<JSONPathFragment> path;

    JSONPath(String path) {
        this.path = compilePathFragments(path);
    }

    public boolean hasMoreFragments(){
        return !path.isEmpty();
    }

    public JSONPathFragment nextFragment(){
        return path.poll();
    }

    private Queue<JSONPathFragment> compilePathFragments(String path) {
        //TODO: this needs some attention but will work for now
        Queue<JSONPathFragment> processed = new LinkedList<JSONPathFragment>();
        for (String fragment : path.split("[\\.|\\[]")) {
            if (fragment.trim().length() > 0) {

                String compiledFragment = fragment.endsWith("]") ? "[" + fragment : fragment;

                processed.add(new JSONPathFragment(compiledFragment));
            }
        }
        return processed;
    }
}
