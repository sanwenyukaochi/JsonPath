package com.jayway.jsonpath.internal.filter;

import static com.jayway.jsonpath.internal.filter.ValueNode.createBooleanNode;
import static com.jayway.jsonpath.internal.filter.ValueNode.createClassNode;
import static com.jayway.jsonpath.internal.filter.ValueNode.createJsonNode;
import static com.jayway.jsonpath.internal.filter.ValueNode.createNullNode;
import static com.jayway.jsonpath.internal.filter.ValueNode.createNumberNode;
import static com.jayway.jsonpath.internal.filter.ValueNode.createPathNode;
import static com.jayway.jsonpath.internal.filter.ValueNode.createPatternNode;
import static com.jayway.jsonpath.internal.filter.ValueNode.createStringNode;
import static com.jayway.jsonpath.internal.filter.ValueNode.createUndefinedNode;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.jayway.jsonpath.BaseTest;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.path.CompiledPath;
import com.jayway.jsonpath.internal.path.PathTokenFactory;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;

@ParameterizedClass(name = "Regexp {0} for {1} node should evaluate to {2}")
@MethodSource("data")
public class RegexpEvaluatorTest extends BaseTest {

    private String regexp;
    private ValueNode valueNode;
    private boolean expectedResult;

    public RegexpEvaluatorTest(String regexp, ValueNode valueNode, boolean expectedResult) {
        this.regexp = regexp;
        this.valueNode = valueNode;
        this.expectedResult = expectedResult;
    }

    @Test
    public void should_evaluate_regular_expression() {

        Evaluator evaluator = EvaluatorFactory.createEvaluator(RelationalOperator.REGEX);
        ValueNode patternNode = createPatternNode(regexp);
        Predicate.PredicateContext ctx = createPredicateContext();

        boolean result = evaluator.evaluate(patternNode, valueNode, ctx);

        assertThat(result, is(equalTo(expectedResult)));
    }

    public static Iterable data() {
        return Arrays.asList(
                new Object[][] {
                    {"/true|false/", createStringNode("true", true), true},
                    {"/9.*9/", createNumberNode("9979"), true},
                    {"/fa.*se/", createBooleanNode("false"), true},
                    {"/Eval.*or/", createClassNode(String.class), false},
                    {"/JsonNode/", createJsonNode(json()), false},
                    {"/PathNode/", createPathNode(path()), false},
                    {"/Undefined/", createUndefinedNode(), false},
                    {"/NullNode/", createNullNode(), false},
                    {"/test/i", createStringNode("tEsT", true), true},
                    {"/test/", createStringNode("tEsT", true), false},
                    {"/\u00de/ui", createStringNode("\u00fe", true), true},
                    {"/\u00de/", createStringNode("\u00fe", true), false},
                    {"/\u00de/i", createStringNode("\u00fe", true), false},
                    {"/test# code/", createStringNode("test", true), false},
                    {"/test# code/x", createStringNode("test", true), true},
                    {"/.*test.*/d", createStringNode("my\rtest", true), true},
                    {"/.*test.*/", createStringNode("my\rtest", true), false},
                    {"/.*tEst.*/is", createStringNode("test\ntest", true), true},
                    {"/.*tEst.*/i", createStringNode("test\ntest", true), false},
                    {"/^\\w+$/U", createStringNode("\u00fe", true), true},
                    {"/^\\w+$/", createStringNode("\u00fe", true), false},
                    {"/^test$\\ntest$/m", createStringNode("test\ntest", true), true}
                });
    }

    private static Path path() {
        return new CompiledPath(PathTokenFactory.createRootPathToken('$'), true);
    }

    private static String json() {
        return "{ 'some': 'JsonNode' }";
    }

    private Predicate.PredicateContext createPredicateContext() {
        return createPredicateContext(new HashMap<String, Object>());
    }
}
