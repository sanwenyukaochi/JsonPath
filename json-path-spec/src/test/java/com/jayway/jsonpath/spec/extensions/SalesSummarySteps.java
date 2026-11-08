package com.jayway.jsonpath.spec.extensions;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SalesSummarySteps {

    private String ordersJson;
    private Object queryResult;

    @Given("报表服务收到以下订单数据")
    public void reportingServiceReceivesOrders(String ordersJson) {
        this.ordersJson = ordersJson;
    }

    @When("报表服务查询路径 {string}")
    public void reportingServiceQueries(String path) {
        queryResult = JsonPath.read(ordersJson, path);
    }

    @Then("统计查询结果数值应为 {double}")
    public void summaryQueryResultShouldBe(double expected) {
        assertThat(queryResult).isInstanceOf(Number.class);
        assertThat(((Number) queryResult).doubleValue()).isEqualTo(expected);
    }
}
