package com.jayway;

import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptTest {


    @Test
    public void script() throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");

        org.junit.jupiter.api.Assertions.assertEquals(false, engine.eval("1 === 2"));
        org.junit.jupiter.api.Assertions.assertEquals(true, engine.eval("'APA' === 'APA'"));
    }
}
