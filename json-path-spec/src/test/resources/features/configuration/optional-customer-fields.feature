Feature: 处理客户资料中的可选字段
  作为客户资料接入服务
  我希望明确控制缺失字段的查询策略
  以便区分允许缺失的数据和必须完整的数据

  Background:
    Given 客户服务收到以下资料
      """
      [
        {
          "customerId": "C-100",
          "email": "alice@example.com",
          "phone": "+86-10086"
        },
        {
          "customerId": "C-200",
          "email": "bob@example.com"
        }
      ]
      """

  Scenario: 默认拒绝读取不存在的确定路径
    When 客户服务使用默认配置查询路径 "$[1].phone"
    Then 客户查询失败类型应为 "PathNotFoundException"

  Scenario: 将缺失的可选联系方式解释为 null
    When 客户服务使用选项 "DEFAULT_PATH_LEAF_TO_NULL" 查询路径 "$[1].phone"
    Then 客户查询结果应为 null

  Scenario: 默认从不确定路径中返回已有的联系方式
    When 客户服务使用默认配置查询路径 "$[*].phone"
    Then 客户查询结果应依次包含
      | +86-10086 |

  Scenario: 完整性检查要求每位客户都具有联系方式
    When 客户服务使用选项 "REQUIRE_PROPERTIES" 查询路径 "$[*].phone"
    Then 客户查询失败类型应为 "PathNotFoundException"
