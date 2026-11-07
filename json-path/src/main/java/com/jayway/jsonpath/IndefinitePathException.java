package com.jayway.jsonpath;

public class IndefinitePathException extends RuntimeException {

    public IndefinitePathException(String path) {
        super("The path " + path + " is not definite");
    }
}
