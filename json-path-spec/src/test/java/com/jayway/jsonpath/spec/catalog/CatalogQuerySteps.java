package com.jayway.jsonpath.spec.catalog;

import static com.jayway.jsonpath.spec.support.SpecResources.read;
import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;

public class CatalogQuerySteps {

    private String catalogJson;
    private Object queryResult;

    @Given("目录服务已加载供应商书店目录")
    public void catalogServiceLoadedSupplierCatalog() {
        catalogJson = read("/fixtures/bookstore-catalog.json");
    }

    @When("目录服务查询路径 {string}")
    public void catalogServiceQueries(String path) {
        queryResult = JsonPath.read(catalogJson, path);
    }

    @Then("目录查询结果应为字符串 {string}")
    public void catalogQueryResultShouldBe(String expected) {
        assertThat(queryResult).isEqualTo(expected);
    }

    @Then("目录查询结果应依次包含")
    public void catalogQueryResultShouldContainInOrder(DataTable expected) {
        assertThat(queryResult).isInstanceOf(List.class);
        assertThat(queryResult).isEqualTo(expected.asList());
    }
}
