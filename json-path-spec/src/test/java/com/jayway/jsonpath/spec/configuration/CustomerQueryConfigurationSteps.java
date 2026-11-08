package com.jayway.jsonpath.spec.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;

public class CustomerQueryConfigurationSteps {

    private String customerJson;
    private Object queryResult;
    private RuntimeException queryFailure;

    @Given("客户服务收到以下资料")
    public void customerServiceReceivesProfiles(String customerJson) {
        this.customerJson = customerJson;
    }

    @When("客户服务使用默认配置查询路径 {string}")
    public void customerServiceQueriesWithDefaultConfiguration(String path) {
        evaluate(Configuration.defaultConfiguration(), path);
    }

    @When("客户服务使用选项 {string} 查询路径 {string}")
    public void customerServiceQueriesWithOption(String optionName, String path) {
        Configuration configuration =
                Configuration.builder().options(Option.valueOf(optionName)).build();
        evaluate(configuration, path);
    }

    @Then("客户查询结果应为字符串 {string}")
    public void customerQueryResultShouldBe(String expected) {
        assertThat(queryFailure).isNull();
        assertThat(queryResult).isEqualTo(expected);
    }

    @Then("客户查询结果应为 null")
    public void customerQueryResultShouldBeNull() {
        assertThat(queryFailure).isNull();
        assertThat(queryResult).isNull();
    }

    @Then("客户查询结果应依次包含")
    public void customerQueryResultShouldContainInOrder(DataTable expected) {
        assertThat(queryFailure).isNull();
        assertThat(queryResult).isInstanceOf(List.class);
        assertThat(queryResult).isEqualTo(expected.asList());
    }

    @Then("客户查询失败类型应为 {string}")
    public void customerQueryShouldFailWith(String expectedSimpleName) {
        assertThat(queryFailure).isNotNull();
        assertThat(queryFailure.getClass().getSimpleName()).isEqualTo(expectedSimpleName);
    }

    private void evaluate(Configuration configuration, String path) {
        queryResult = null;
        queryFailure = null;
        try {
            queryResult = JsonPath.using(configuration).parse(customerJson).read(path);
        } catch (RuntimeException exception) {
            queryFailure = exception;
        }
    }
}
