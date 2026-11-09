Feature: 按路径读取 JSON 文档
  作为需要处理外部 JSON 数据的应用
  我希望使用 JSONPath 读取嵌套字段
  以便无需为每种文档结构创建固定数据类

  Scenario: 读取订单中的客户姓名
    Given 收到以下订单 JSON
      """
      {
        "customer": {
          "name": "Alice"
        }
      }
      """
    When 读取路径 "$.customer.name"
    Then 查询结果应为字符串 "Alice"
