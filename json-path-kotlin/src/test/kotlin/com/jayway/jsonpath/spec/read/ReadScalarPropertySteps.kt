package com.jayway.jsonpath.spec.read

import com.jayway.jsonpath.JsonPath
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals

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

    @Then("查询结果应为字符串 {string}")
    fun queryResultShouldBeString(expected: String) {
        assertEquals(expected, queryResult)
    }
}
