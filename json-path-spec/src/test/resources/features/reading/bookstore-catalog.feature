Feature: 为书店页面读取商品目录
  作为在线书店的商品目录服务
  我希望从供应商目录中准确选取页面所需的数据
  以便不同页面无需自行遍历完整 JSON 文档

  Background:
    Given 目录服务已加载供应商书店目录

  Scenario: 为商品详情页读取指定图书标题
    When 目录服务查询路径 "$.store.book[2].title"
    Then 目录查询结果应为字符串 "Moby Dick"

  Scenario: 为作者导航收集全部作者
    When 目录服务查询路径 "$.store.book[*].author"
    Then 目录查询结果应依次包含
      | Nigel Rees       |
      | Evelyn Waugh     |
      | Herman Melville  |
      | J. R. R. Tolkien |

  Scenario: 为首页推荐区读取前两本图书
    When 目录服务查询路径 "$.store.book[0:2].title"
    Then 目录查询结果应依次包含
      | Sayings of the Century |
      | Sword of Honour        |
