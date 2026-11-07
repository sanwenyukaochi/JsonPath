package com.jayway.jsonpath.matchers.helpers;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.ClassLoader.getSystemResourceAsStream;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.commons.io.IOUtils;

public class ResourceHelpers {
    public static String resource(String resource) {
        try {
            return IOUtils.toString(getSystemResourceAsStream(resource));
        } catch (IOException e) {
            throw new AssertionError("Resource not found: " + e.getMessage());
        }
    }

    public static File resourceAsFile(String resource) {
        try {
            URL systemResource = getSystemResource(resource);
            return new File(systemResource.toURI());
        } catch (URISyntaxException e) {
            throw new AssertionError("URI syntax error:" + e.getMessage());
        }
    }
}
