package com.jayway.jsonpath;

import java.util.LinkedList;
import java.util.List;

public class PathUtil {

    public static boolean isPathDefinite(String jsonPath) {
        return !jsonPath.replaceAll("\"[^\"\\\\\\n\r]*\"", "")
                .matches(".*(\\.\\.|\\*|\\[[\\\\/]|\\?|,|:\\s?\\]|\\[\\s?:|>|\\(|<|=|\\+).*");
    }

    public static List<String> splitPath(String jsonPath) {

        LinkedList<String> fragments = new LinkedList<String>();

        if (!jsonPath.startsWith("$.")) {
            jsonPath = "$." + jsonPath;
        }

        jsonPath =
                jsonPath.replace("..", ".~.")
                        .replace("[", ".[")
                        .replace("@.", "@")
                        .replace("['", "")
                        .replace("']", "");

        String[] split = jsonPath.split("\\.");

        for (int i = 0; i < split.length; i++) {
            if (split[i].trim().isEmpty()) {
                continue;
            }
            fragments.add(split[i].replace("@", "@.").replace("~", ".."));
        }
        return fragments;
    }
}
