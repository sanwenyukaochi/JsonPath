package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonProviderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionEvalTest {

    private static final Logger logger = LoggerFactory.getLogger(ExpressionEvalTest.class);

    public static final String DOCUMENT =
"""
{
    "characters": [
        {
            "aliases": [],\s
            "name": "Kleeg Lars",\s
            "occupation": "Moisture farmer",\s
            "offspring": []
        },\s
        {
            "aliases": [],\s
            "name": "Shmi Skywalker",\s
            "occupation": "Virgin mother",\s
            "offspring": [
                "AnakinSkywalker"
            ]
        },\s
        {
            "aliases": [
                "Darth Vader"
            ],\s
            "name": "Annakin Skywalker",\s
            "occupation": "Hand of the Emperor, Lord of the Sith",\s
            "offspring": [
                "Luke Skywalker",\s
                "LeiaOrgana"
            ]
        },\s
        {
            "aliases": [
                "Nerf herder"
            ],\s
            "name": "Luke Skywalker",\s
            "occupation": "Farm boy",\s
            "offspring": null
        },\s
        {
            "aliases": [
                "Your Highness"
            ],\s
            "name": "Leia Organa",\s
            "occupation": "Senator"\s
        }
    ]
}
""";

    private static final JsonProvider jp = JsonProviderFactory.createProvider();
}
