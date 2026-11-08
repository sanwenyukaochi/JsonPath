Feature: 从订单 JSON 生成销售摘要
  作为销售报表服务
  我希望直接聚合订单 JSON 中的金额和数量
  以便生成不依赖额外遍历代码的销售摘要

  Background:
    Given 报表服务收到以下订单数据
      """
      {
        "orders": [
          {"orderId": "O-100", "total": 12.5},
          {"orderId": "O-101", "total": 20.0},
          {"orderId": "O-102", "total": 7.5},
          {"orderId": "O-103", "total": 10.0}
        ]
      }
      """

  Scenario: 计算订单销售总额
    When 报表服务查询路径 "$.sum($.orders[*].total)"
    Then 统计查询结果数值应为 50.0

  Scenario: 计算平均客单价
    When 报表服务查询路径 "$.avg($.orders[*].total)"
    Then 统计查询结果数值应为 12.5

  Scenario: 识别最低订单金额
    When 报表服务查询路径 "$.min($.orders[*].total)"
    Then 统计查询结果数值应为 7.5

  Scenario: 统计订单数量
    When 报表服务查询路径 "$.orders.length()"
    Then 统计查询结果数值应为 4.0
