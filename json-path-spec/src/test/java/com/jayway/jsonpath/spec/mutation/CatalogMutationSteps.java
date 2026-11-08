package com.jayway.jsonpath.spec.mutation;

import static com.jayway.jsonpath.spec.support.SpecResources.read;
import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;

public class CatalogMutationSteps {

    private DocumentContext catalog;

    @Given("商品维护服务已加载供应商书店目录")
    public void catalogMaintenanceLoadedSupplierCatalog() {
        catalog = JsonPath.parse(read("/fixtures/bookstore-catalog.json"));
    }

    @When("商品维护服务将路径 {string} 修改为字符串 {string}")
    public void catalogMaintenanceSetsString(String path, String value) {
        catalog.set(path, value);
    }

    @When("商品维护服务删除路径 {string}")
    public void catalogMaintenanceDeletesPath(String path) {
        catalog.delete(path);
    }

    @When("商品维护服务在对象路径 {string} 写入属性 {string} 和字符串 {string}")
    public void catalogMaintenanceAddsProperty(String path, String property, String value) {
        catalog.put(path, property, value);
    }

    @Then("修改后路径 {string} 应为字符串 {string}")
    public void updatedPathShouldBe(String path, String expected) {
        assertThat(catalog.<Object>read(path)).isEqualTo(expected);
    }

    @Then("修改后路径 {string} 应依次包含")
    public void updatedPathShouldContainInOrder(String path, DataTable expected) {
        Object result = catalog.read(path);
        assertThat(result).isInstanceOf(List.class);
        assertThat(result).isEqualTo(expected.asList());
    }
}
