# 当前项目与标准项目有效差异报告

## 对比基准

- 当前项目：`/Users/sanwenyukaochi/Desktop/JAVA_PROJECT/JsonPath`
- 当前提交：`cb9b25db125a343a84eac4983ed5bb48b695dc1c`
- 标准项目：`/Users/sanwenyukaochi/Desktop/TMP_PROJECT/JsonPath`
- 标准提交：`62a4c9f0f65ba3f625aa0867d64c528ba72d09ec`
- 排除：Git 元数据、Gradle/构建产物、IDE 与系统缓存、本地 `AGENT.md`、报告自身。

## 判定规则

| 标记 | 含义 |
|:---:|---|
| ✅ | 有效内容一致；忽略导入顺序、通配符与显式导入差别、空格、换行、注释、等价修饰符顺序及 JSON 格式/键顺序。 |
| 🟡 | 项目有意保留的工程差异，包括 Kotlin DSL、Gradle/JDK/JUnit/依赖版本、CI、开发工具和新版测试写法。 |
| ❌ | 真正差异：运行或测试行为不同，或者标准项目的重要文件在当前项目中缺失。 |

## 结论

| 分类 | 数量 |
|---|---:|
| ✅ 有效一致 | 217 |
| 🟡 有意差异 | 27 |
| ❌ 真正差异 | 6 |
| 合计配对/单侧条目 | 250 |

- 119 个生产 Java 文件全部有效一致；未发现核心算法、分支、循环、调用顺序、返回值或异常包装意图偏离。
- 其中词法复核发现的 17 个主代码差异，均属于通配符/显式导入、未使用导入删除，或 `final static` 与 `static final` 等价顺序。
- 85 个测试 Java 文件的测试意图一致；JUnit、Hamcrest、文本块和断言写法差异归入项目允许范围。
- 13 个 JSON 测试资源解析后的数据一致；缩进、转义表示和对象键顺序不计为差异。
- Jackson 2/3 双适配职责已对齐：旧名称 Provider 面向 `com.fasterxml.jackson` 2.x，`Jackson3*` 面向 `tools.jackson` 3.x。

## 真正差异

| 当前项目文件名称 | 标准项目文件名称 | 差异说明 |
|---|---|---|
| `json-path/src/test/resources/simplelogger.properties` | 同路径 | 当前启用 debug/trace；标准项目全部处于注释状态，会改变测试日志输出。 |
| `.github/CHANGELOG.md` | `changelog.md` | 当前文件仅保留早期 0.5.x 内容；标准文件还包含 1.0.0 至 2.2.0 等版本记录。 |
| — | `LICENSE` | 当前项目缺少许可证正文；公开发布前建议恢复。 |
| — | `README.md` | 当前项目缺少根项目说明。 |
| — | `json-path-assert/README.md` | 当前项目缺少断言模块说明。 |
| — | `jsonpath.png` | 当前项目缺少标准项目图片资源。 |

## 有意保留的差异

- 构建脚本：当前项目采用 Kotlin DSL；标准项目采用 Groovy DSL。
- 工具链：当前项目使用 Gradle 9.9、JDK 27 编译和 `--release 17`；标准项目使用 Gradle 8.5。
- 测试与依赖：当前项目采用较新的 JUnit、Hamcrest、Jackson 2/3 等版本，并做必要 API 适配。
- CI：当前项目验证 Java 17 和 27，并使用滚动更新的 `ubuntu-latest`；标准 CI 使用不同的版本矩阵与 Runner 配置。
- 工程增强：开发容器、SDKMAN、Renovate、GitHub 社区文件、架构文档等仅存在于当前项目。
- 已淘汰流程：`.travis.yml`、`Procfile`、`system.properties` 仅存在于标准项目，当前项目不恢复。

## 全部文件清单

| 当前项目文件名称 | 标准项目文件名称 | 是否一致 |
|---|---|:---:|
| `.devcontainer/devcontainer.json` | — | 🟡 |
| `.gitattributes` | — | 🟡 |
| `.github/CHANGELOG.md` | `changelog.md` | ❌ |
| `.github/CODEOWNERS` | — | 🟡 |
| `.github/CODE_OF_CONDUCT.md` | — | 🟡 |
| `.github/CONTRIBUTING.md` | — | 🟡 |
| `.github/FUNDING.yaml` | — | 🟡 |
| `.github/ISSUE_TEMPLATE/config.yaml` | — | 🟡 |
| `.github/ISSUE_TEMPLATE/✨-feature-request.md` | — | 🟡 |
| `.github/ISSUE_TEMPLATE/🐞-bug-report.md` | — | 🟡 |
| `.github/ISSUE_TEMPLATE/📝-documentation-improvement.md` | — | 🟡 |
| `.github/workflows/ci.yml` | `.github/workflows/ci.yml` | 🟡 |
| `.gitignore` | `.gitignore` | 🟡 |
| `.sdkmanrc` | — | 🟡 |
| `ARCHITECTURE.md` | — | 🟡 |
| `build.gradle.kts` | `build.gradle` | 🟡 |
| `gradle.properties` | — | 🟡 |
| `gradle/wrapper/gradle-wrapper.jar` | `gradle/wrapper/gradle-wrapper.jar` | 🟡 |
| `gradle/wrapper/gradle-wrapper.properties` | `gradle/wrapper/gradle-wrapper.properties` | 🟡 |
| `gradlew` | `gradlew` | 🟡 |
| `gradlew.bat` | `gradlew.bat` | 🟡 |
| `json-path-assert/build.gradle.kts` | `json-path-assert/build.gradle` | 🟡 |
| `json-path-assert/src/main/java/com/jayway/jsonassert/JsonAssert.java` | `json-path-assert/src/main/java/com/jayway/jsonassert/JsonAssert.java` | ✅ |
| `json-path-assert/src/main/java/com/jayway/jsonassert/JsonAsserter.java` | `json-path-assert/src/main/java/com/jayway/jsonassert/JsonAsserter.java` | ✅ |
| `json-path-assert/src/main/java/com/jayway/jsonassert/impl/JsonAsserterImpl.java` | `json-path-assert/src/main/java/com/jayway/jsonassert/impl/JsonAsserterImpl.java` | ✅ |
| `json-path-assert/src/main/java/com/jayway/jsonassert/impl/matcher/CollectionMatcher.java` | `json-path-assert/src/main/java/com/jayway/jsonassert/impl/matcher/CollectionMatcher.java` | ✅ |
| `json-path-assert/src/main/java/com/jayway/jsonassert/impl/matcher/IsCollectionWithSize.java` | `json-path-assert/src/main/java/com/jayway/jsonassert/impl/matcher/IsCollectionWithSize.java` | ✅ |
| `json-path-assert/src/main/java/com/jayway/jsonassert/impl/matcher/IsEmptyCollection.java` | `json-path-assert/src/main/java/com/jayway/jsonassert/impl/matcher/IsEmptyCollection.java` | ✅ |
| `json-path-assert/src/main/java/com/jayway/jsonassert/impl/matcher/IsMapContainingKey.java` | `json-path-assert/src/main/java/com/jayway/jsonassert/impl/matcher/IsMapContainingKey.java` | ✅ |
| `json-path-assert/src/main/java/com/jayway/jsonassert/impl/matcher/IsMapContainingValue.java` | `json-path-assert/src/main/java/com/jayway/jsonassert/impl/matcher/IsMapContainingValue.java` | ✅ |
| `json-path-assert/src/main/java/com/jayway/jsonassert/impl/matcher/MapTypeSafeMatcher.java` | `json-path-assert/src/main/java/com/jayway/jsonassert/impl/matcher/MapTypeSafeMatcher.java` | ✅ |
| `json-path-assert/src/main/java/com/jayway/jsonpath/matchers/IsJson.java` | `json-path-assert/src/main/java/com/jayway/jsonpath/matchers/IsJson.java` | ✅ |
| `json-path-assert/src/main/java/com/jayway/jsonpath/matchers/JsonPathMatchers.java` | `json-path-assert/src/main/java/com/jayway/jsonpath/matchers/JsonPathMatchers.java` | ✅ |
| `json-path-assert/src/main/java/com/jayway/jsonpath/matchers/WithJsonPath.java` | `json-path-assert/src/main/java/com/jayway/jsonpath/matchers/WithJsonPath.java` | ✅ |
| `json-path-assert/src/main/java/com/jayway/jsonpath/matchers/WithoutJsonPath.java` | `json-path-assert/src/main/java/com/jayway/jsonpath/matchers/WithoutJsonPath.java` | ✅ |
| `json-path-assert/src/test/java/com/jayway/jsonassert/JsonAssertTest.java` | `json-path-assert/src/test/java/com/jayway/jsonassert/JsonAssertTest.java` | ✅ |
| `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/DemoTest.java` | `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/DemoTest.java` | ✅ |
| `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/HasNoJsonPathTest.java` | `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/HasNoJsonPathTest.java` | ✅ |
| `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/IsJsonFileTest.java` | `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/IsJsonFileTest.java` | ✅ |
| `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/IsJsonStringTest.java` | `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/IsJsonStringTest.java` | ✅ |
| `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/IsJsonTest.java` | `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/IsJsonTest.java` | ✅ |
| `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/JsonPathMatchersTest.java` | `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/JsonPathMatchersTest.java` | ✅ |
| `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/WithJsonPathTest.java` | `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/WithJsonPathTest.java` | ✅ |
| `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/WithoutJsonPathTest.java` | `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/WithoutJsonPathTest.java` | ✅ |
| `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/helpers/ResourceHelpers.java` | `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/helpers/ResourceHelpers.java` | ✅ |
| `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/helpers/StrictParsingConfiguration.java` | `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/helpers/StrictParsingConfiguration.java` | ✅ |
| `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/helpers/TestingMatchers.java` | `json-path-assert/src/test/java/com/jayway/jsonpath/matchers/helpers/TestingMatchers.java` | ✅ |
| `json-path-assert/src/test/resources/books.json` | `json-path-assert/src/test/resources/books.json` | ✅ |
| `json-path-assert/src/test/resources/example.json` | `json-path-assert/src/test/resources/example.json` | ✅ |
| `json-path-assert/src/test/resources/invalid.json` | `json-path-assert/src/test/resources/invalid.json` | ✅ |
| `json-path-assert/src/test/resources/links.json` | `json-path-assert/src/test/resources/links.json` | ✅ |
| `json-path-assert/src/test/resources/lotto.json` | `json-path-assert/src/test/resources/lotto.json` | ✅ |
| `json-path/build.gradle.kts` | `json-path/build.gradle` | 🟡 |
| `json-path/src/main/java/com/jayway/jsonpath/Configuration.java` | `json-path/src/main/java/com/jayway/jsonpath/Configuration.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/Criteria.java` | `json-path/src/main/java/com/jayway/jsonpath/Criteria.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/DocumentContext.java` | `json-path/src/main/java/com/jayway/jsonpath/DocumentContext.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/EvaluationListener.java` | `json-path/src/main/java/com/jayway/jsonpath/EvaluationListener.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/Filter.java` | `json-path/src/main/java/com/jayway/jsonpath/Filter.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/InvalidCriteriaException.java` | `json-path/src/main/java/com/jayway/jsonpath/InvalidCriteriaException.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/InvalidJsonException.java` | `json-path/src/main/java/com/jayway/jsonpath/InvalidJsonException.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/InvalidModificationException.java` | `json-path/src/main/java/com/jayway/jsonpath/InvalidModificationException.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/InvalidPathException.java` | `json-path/src/main/java/com/jayway/jsonpath/InvalidPathException.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/JsonPath.java` | `json-path/src/main/java/com/jayway/jsonpath/JsonPath.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/JsonPathException.java` | `json-path/src/main/java/com/jayway/jsonpath/JsonPathException.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/MapFunction.java` | `json-path/src/main/java/com/jayway/jsonpath/MapFunction.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/Option.java` | `json-path/src/main/java/com/jayway/jsonpath/Option.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/ParseContext.java` | `json-path/src/main/java/com/jayway/jsonpath/ParseContext.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/PathNotFoundException.java` | `json-path/src/main/java/com/jayway/jsonpath/PathNotFoundException.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/Predicate.java` | `json-path/src/main/java/com/jayway/jsonpath/Predicate.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/ReadContext.java` | `json-path/src/main/java/com/jayway/jsonpath/ReadContext.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/TypeRef.java` | `json-path/src/main/java/com/jayway/jsonpath/TypeRef.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/ValueCompareException.java` | `json-path/src/main/java/com/jayway/jsonpath/ValueCompareException.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/WriteContext.java` | `json-path/src/main/java/com/jayway/jsonpath/WriteContext.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/CharacterIndex.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/CharacterIndex.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/DefaultsImpl.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/DefaultsImpl.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/EvaluationAbortException.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/EvaluationAbortException.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/EvaluationContext.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/EvaluationContext.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/JsonContext.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/JsonContext.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/JsonFormatter.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/JsonFormatter.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/ParseContextImpl.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/ParseContextImpl.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/Path.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/Path.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/PathRef.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/PathRef.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/Utils.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/Utils.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/filter/Evaluator.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/filter/Evaluator.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/filter/EvaluatorFactory.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/filter/EvaluatorFactory.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/filter/ExpressionNode.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/filter/ExpressionNode.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/filter/FilterCompiler.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/filter/FilterCompiler.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/filter/LogicalExpressionNode.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/filter/LogicalExpressionNode.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/filter/LogicalOperator.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/filter/LogicalOperator.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/filter/PatternFlag.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/filter/PatternFlag.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/filter/RelationalExpressionNode.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/filter/RelationalExpressionNode.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/filter/RelationalOperator.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/filter/RelationalOperator.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/filter/ValueNode.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/filter/ValueNode.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/filter/ValueNodes.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/filter/ValueNodes.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/ParamType.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/ParamType.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/Parameter.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/Parameter.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/PassthruPathFunction.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/PassthruPathFunction.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/PathFunction.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/PathFunction.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/PathFunctionFactory.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/PathFunctionFactory.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/json/Append.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/json/Append.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/json/KeySetFunction.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/json/KeySetFunction.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/latebinding/ILateBindingValue.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/latebinding/ILateBindingValue.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/latebinding/JsonLateBindingValue.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/latebinding/JsonLateBindingValue.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/latebinding/PathLateBindingValue.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/latebinding/PathLateBindingValue.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/numeric/AbstractAggregation.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/numeric/AbstractAggregation.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/numeric/Average.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/numeric/Average.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/numeric/Max.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/numeric/Max.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/numeric/Min.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/numeric/Min.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/numeric/StandardDeviation.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/numeric/StandardDeviation.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/numeric/Sum.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/numeric/Sum.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/sequence/AbstractSequenceAggregation.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/sequence/AbstractSequenceAggregation.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/sequence/First.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/sequence/First.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/sequence/Index.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/sequence/Index.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/sequence/Last.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/sequence/Last.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/text/Concatenate.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/text/Concatenate.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/function/text/Length.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/function/text/Length.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/ArrayIndexOperation.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/ArrayIndexOperation.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/ArrayIndexToken.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/ArrayIndexToken.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/ArrayPathToken.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/ArrayPathToken.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/ArraySliceOperation.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/ArraySliceOperation.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/ArraySliceToken.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/ArraySliceToken.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/CompiledPath.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/CompiledPath.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/EvaluationContextImpl.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/EvaluationContextImpl.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/FunctionPathToken.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/FunctionPathToken.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/PathCompiler.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/PathCompiler.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/PathToken.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/PathToken.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/PathTokenAppender.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/PathTokenAppender.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/PathTokenFactory.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/PathTokenFactory.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/PredicateContextImpl.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/PredicateContextImpl.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/PredicatePathToken.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/PredicatePathToken.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/PropertyPathToken.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/PropertyPathToken.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/RootPathToken.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/RootPathToken.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/ScanPathToken.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/ScanPathToken.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/internal/path/WildcardPathToken.java` | `json-path/src/main/java/com/jayway/jsonpath/internal/path/WildcardPathToken.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/cache/Cache.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/cache/Cache.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/cache/CacheProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/cache/CacheProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/cache/LRUCache.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/cache/LRUCache.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/cache/NOOPCache.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/cache/NOOPCache.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/json/AbstractJsonProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/json/AbstractJsonProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/json/GsonJsonProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/json/GsonJsonProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/json/Jackson3JsonNodeJsonProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/json/Jackson3JsonNodeJsonProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/json/Jackson3JsonProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/json/Jackson3JsonProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/json/JacksonJsonNodeJsonProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/json/JacksonJsonNodeJsonProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/json/JacksonJsonProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/json/JacksonJsonProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/json/JakartaJsonProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/json/JakartaJsonProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/json/JettisonProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/json/JettisonProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/json/JsonOrgJsonProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/json/JsonOrgJsonProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/json/JsonProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/json/JsonProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/json/JsonSmartJsonProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/json/JsonSmartJsonProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/json/TapestryJsonProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/json/TapestryJsonProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/GsonMappingProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/GsonMappingProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/Jackson3MappingProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/Jackson3MappingProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/JacksonMappingProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/JacksonMappingProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/JakartaMappingProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/JakartaMappingProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/JsonOrgMappingProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/JsonOrgMappingProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/JsonSmartMappingProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/JsonSmartMappingProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/MappingException.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/MappingException.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/MappingProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/MappingProvider.java` | ✅ |
| `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/TapestryMappingProvider.java` | `json-path/src/main/java/com/jayway/jsonpath/spi/mapper/TapestryMappingProvider.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/BaseTest.java` | `json-path/src/test/java/com/jayway/jsonpath/BaseTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/Configurations.java` | `json-path/src/test/java/com/jayway/jsonpath/Configurations.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/DeepScanTest.java` | `json-path/src/test/java/com/jayway/jsonpath/DeepScanTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/EscapeTest.java` | `json-path/src/test/java/com/jayway/jsonpath/EscapeTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/EvaluationListenerTest.java` | `json-path/src/test/java/com/jayway/jsonpath/EvaluationListenerTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/FilterCompilerTest.java` | `json-path/src/test/java/com/jayway/jsonpath/FilterCompilerTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/FilterParseTest.java` | `json-path/src/test/java/com/jayway/jsonpath/FilterParseTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/FilterTest.java` | `json-path/src/test/java/com/jayway/jsonpath/FilterTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/GsonJsonProviderTest.java` | `json-path/src/test/java/com/jayway/jsonpath/GsonJsonProviderTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/InlineFilterTest.java` | `json-path/src/test/java/com/jayway/jsonpath/InlineFilterTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/Issue_487.java` | `json-path/src/test/java/com/jayway/jsonpath/Issue_487.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/Issue_537.java` | `json-path/src/test/java/com/jayway/jsonpath/Issue_537.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/Issue_721.java` | `json-path/src/test/java/com/jayway/jsonpath/Issue_721.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/Issue_762.java` | `json-path/src/test/java/com/jayway/jsonpath/Issue_762.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/Issue_786.java` | `json-path/src/test/java/com/jayway/jsonpath/Issue_786.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/Issue_970.java` | `json-path/src/test/java/com/jayway/jsonpath/Issue_970.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/Issue_973.java` | `json-path/src/test/java/com/jayway/jsonpath/Issue_973.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/Jackson3JsonNodeJsonProviderMapperSupportTest.java` | `json-path/src/test/java/com/jayway/jsonpath/Jackson3JsonNodeJsonProviderMapperSupportTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/Jackson3JsonNodeJsonProviderTest.java` | `json-path/src/test/java/com/jayway/jsonpath/Jackson3JsonNodeJsonProviderTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/Jackson3Test.java` | `json-path/src/test/java/com/jayway/jsonpath/Jackson3Test.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/JacksonJsonNodeJsonProviderMapperSupportTest.java` | `json-path/src/test/java/com/jayway/jsonpath/JacksonJsonNodeJsonProviderMapperSupportTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/JacksonJsonNodeJsonProviderTest.java` | `json-path/src/test/java/com/jayway/jsonpath/JacksonJsonNodeJsonProviderTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/JacksonTest.java` | `json-path/src/test/java/com/jayway/jsonpath/JacksonTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/JakartaJsonProviderTest.java` | `json-path/src/test/java/com/jayway/jsonpath/JakartaJsonProviderTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/JsonOrgJsonProviderTest.java` | `json-path/src/test/java/com/jayway/jsonpath/JsonOrgJsonProviderTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/JsonProviderTest.java` | `json-path/src/test/java/com/jayway/jsonpath/JsonProviderTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/JsonProviderTestObjectMapping.java` | `json-path/src/test/java/com/jayway/jsonpath/JsonProviderTestObjectMapping.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/MapperTest.java` | `json-path/src/test/java/com/jayway/jsonpath/MapperTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/MultiPropTest.java` | `json-path/src/test/java/com/jayway/jsonpath/MultiPropTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/OptionsTest.java` | `json-path/src/test/java/com/jayway/jsonpath/OptionsTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/PathCompilerTest.java` | `json-path/src/test/java/com/jayway/jsonpath/PathCompilerTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/PredicateTest.java` | `json-path/src/test/java/com/jayway/jsonpath/PredicateTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/ProviderInTest.java` | `json-path/src/test/java/com/jayway/jsonpath/ProviderInTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/ReadContextTest.java` | `json-path/src/test/java/com/jayway/jsonpath/ReadContextTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/ReturnTypeTest.java` | `json-path/src/test/java/com/jayway/jsonpath/ReturnTypeTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/ScientificNotationTest.java` | `json-path/src/test/java/com/jayway/jsonpath/ScientificNotationTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/TapestryJsonProviderTest.java` | `json-path/src/test/java/com/jayway/jsonpath/TapestryJsonProviderTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/TestSuppressExceptions.java` | `json-path/src/test/java/com/jayway/jsonpath/TestSuppressExceptions.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/TestUtils.java` | `json-path/src/test/java/com/jayway/jsonpath/TestUtils.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/WriteTest.java` | `json-path/src/test/java/com/jayway/jsonpath/WriteTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/JsonContextTest.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/JsonContextTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/UtilsTest.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/UtilsTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/filter/PatternFlagTest.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/filter/PatternFlagTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/filter/RegexpEvaluatorTest.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/filter/RegexpEvaluatorTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/filter/RelationalOperatorTest.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/filter/RelationalOperatorTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/function/BaseFunctionTest.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/function/BaseFunctionTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/function/Issue191.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/function/Issue191.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/function/Issue234.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/function/Issue234.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/function/Issue273.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/function/Issue273.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/function/Issue612.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/function/Issue612.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/function/Issue629.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/function/Issue629.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/function/Issue680.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/function/Issue680.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/function/JSONEntityPathFunctionTest.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/function/JSONEntityPathFunctionTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/function/KeySetFunctionTest.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/function/KeySetFunctionTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/function/NestedFunctionTest.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/function/NestedFunctionTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/function/NumericPathFunctionTest.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/function/NumericPathFunctionTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/function/SequentialPathFunctionTest.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/function/SequentialPathFunctionTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/internal/path/PathTokenTest.java` | `json-path/src/test/java/com/jayway/jsonpath/internal/path/PathTokenTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/issue_613.java` | `json-path/src/test/java/com/jayway/jsonpath/issue_613.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/ArraySlicingTest.java` | `json-path/src/test/java/com/jayway/jsonpath/old/ArraySlicingTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/ComplianceTest.java` | `json-path/src/test/java/com/jayway/jsonpath/old/ComplianceTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/FilterTest.java` | `json-path/src/test/java/com/jayway/jsonpath/old/FilterTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/IssuesTest.java` | `json-path/src/test/java/com/jayway/jsonpath/old/IssuesTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/JsonPathTest.java` | `json-path/src/test/java/com/jayway/jsonpath/old/JsonPathTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/JsonProviderTest.java` | `json-path/src/test/java/com/jayway/jsonpath/old/JsonProviderTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/NullHandlingTest.java` | `json-path/src/test/java/com/jayway/jsonpath/old/NullHandlingTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/internal/ArrayIndexFilterTest.java` | `json-path/src/test/java/com/jayway/jsonpath/old/internal/ArrayIndexFilterTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/internal/ArrayPathTokenTest.java` | `json-path/src/test/java/com/jayway/jsonpath/old/internal/ArrayPathTokenTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/internal/PredicatePathTokenTest.java` | `json-path/src/test/java/com/jayway/jsonpath/old/internal/PredicatePathTokenTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/internal/PropertyPathTokenTest.java` | `json-path/src/test/java/com/jayway/jsonpath/old/internal/PropertyPathTokenTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/internal/ScanPathTokenTest.java` | `json-path/src/test/java/com/jayway/jsonpath/old/internal/ScanPathTokenTest.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/internal/TestBase.java` | `json-path/src/test/java/com/jayway/jsonpath/old/internal/TestBase.java` | ✅ |
| `json-path/src/test/java/com/jayway/jsonpath/old/internal/TestInternal3.java` | `json-path/src/test/java/com/jayway/jsonpath/old/internal/TestInternal3.java` | ✅ |
| `json-path/src/test/resources/issue_191.json` | `json-path/src/test/resources/issue_191.json` | ✅ |
| `json-path/src/test/resources/issue_24.json` | `json-path/src/test/resources/issue_24.json` | ✅ |
| `json-path/src/test/resources/issue_76.json` | `json-path/src/test/resources/issue_76.json` | ✅ |
| `json-path/src/test/resources/issue_76_2.json` | `json-path/src/test/resources/issue_76_2.json` | ✅ |
| `json-path/src/test/resources/json-test-doc.json` | `json-path/src/test/resources/json-test-doc.json` | ✅ |
| `json-path/src/test/resources/json_array.json` | `json-path/src/test/resources/json_array.json` | ✅ |
| `json-path/src/test/resources/json_array_multiple_delete.json` | `json-path/src/test/resources/json_array_multiple_delete.json` | ✅ |
| `json-path/src/test/resources/keyset.json` | `json-path/src/test/resources/keyset.json` | ✅ |
| `json-path/src/test/resources/simplelogger.properties` | `json-path/src/test/resources/simplelogger.properties` | ❌ |
| `renovate.json` | — | 🟡 |
| `settings.gradle.kts` | `settings.gradle` | 🟡 |
| — | `.travis.yml` | 🟡 |
| — | `LICENSE` | ❌ |
| — | `Procfile` | 🟡 |
| — | `README.md` | ❌ |
| — | `json-path-assert/README.md` | ❌ |
| — | `jsonpath.png` | ❌ |
| — | `system.properties` | 🟡 |
