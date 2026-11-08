Feature: 为调用方提供统一的查询结果形状
  作为对外提供 JSON 查询能力的接口服务
  我希望按接口契约控制确定路径的返回形状
  以便调用方可以稳定地处理单值或列表结果

  Background:
    Given 客户服务收到以下资料
      """
      [
        {
          "customerId": "C-100",
          "email": "alice@example.com"
        }
      ]
      """

  Scenario: 默认以标量返回确定路径的值
    When 客户服务使用默认配置查询路径 "$[0].email"
    Then 客户查询结果应为字符串 "alice@example.com"

  Scenario: 接口约定始终以列表返回结果
    When 客户服务使用选项 "ALWAYS_RETURN_LIST" 查询路径 "$[0].email"
    Then 客户查询结果应依次包含
      | alice@example.com |
