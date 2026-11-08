Feature: 在发布前修正供应商商品目录
  作为在线书店的商品维护服务
  我希望通过路径对已解析的商品目录进行定点修改
  以便在不重建整个 JSON 文档的情况下应用业务修正

  Background:
    Given 商品维护服务已加载供应商书店目录

  Scenario: 修正指定图书的标题
    When 商品维护服务将路径 "$.store.book[2].title" 修改为字符串 "Moby-Dick"
    Then 修改后路径 "$.store.book[2].title" 应为字符串 "Moby-Dick"

  Scenario: 下架超过渠道价格上限的图书
    When 商品维护服务删除路径 "$.store.book[?(@.price > 20)]"
    Then 修改后路径 "$.store.book[*].title" 应依次包含
      | Sayings of the Century |
      | Sword of Honour        |
      | Moby Dick              |

  Scenario: 为指定图书补充库存状态
    When 商品维护服务在对象路径 "$.store.book[2]" 写入属性 "availability" 和字符串 "in-stock"
    Then 修改后路径 "$.store.book[2].availability" 应为字符串 "in-stock"
