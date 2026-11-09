package com.jayway.jsonpath.spec.read

import com.jayway.jsonpath.JsonPath
import io.cucumber.java.en.Given
import io.cucumber.java.en.When

class ReadScalarPropertySteps {
    private lateinit var jsonDocument: String

    private var queryResult: Any? = null

    @Given("收到以下订单 JSON")
    fun receiveOrderJson(document: String) {
        jsonDocument = document
    }

    @When("读取路径 {string}")
    fun readPath(path: String) {
        queryResult =
            JsonPath
                .parse(jsonDocument)
                .read(path)
    }
}
