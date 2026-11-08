Feature: 按书店业务规则筛选图书
  作为在线书店的商品检索服务
  我希望直接在供应商目录上表达筛选条件
  以便只向下游功能返回满足业务规则的图书

  Background:
    Given 目录服务已加载供应商书店目录

  Scenario: 为预算筛选器找出十元以下的小说
    When 目录服务查询路径 "$.store.book[?(@.category == 'fiction' && @.price < 10)].title"
    Then 目录查询结果应依次包含
      | Moby Dick |

  Scenario: 为外部渠道选择具有 ISBN 的图书
    When 目录服务查询路径 "$.store.book[?(@.isbn)].title"
    Then 目录查询结果应依次包含
      | Moby Dick             |
      | The Lord of the Rings |

  Scenario: 使用目录中的价格上限选择非昂贵图书
    When 目录服务查询路径 "$.store.book[?(@.price <= $.expensive)].title"
    Then 目录查询结果应依次包含
      | Sayings of the Century |
      | Moby Dick              |
