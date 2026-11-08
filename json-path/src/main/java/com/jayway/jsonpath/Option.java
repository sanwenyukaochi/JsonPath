package com.jayway.jsonpath;

/**
 * <strong>en:</strong> Defines options that customize how a JSONPath expression is evaluated and
 * how its result is returned. Options are supplied through {@link Configuration}.
 *
 * <p><strong>zh:</strong> 定义 JSONPath 表达式的求值方式及其结果返回方式。选项通过 {@link Configuration} 提供。
 */
public enum Option {
    /**
     * <strong>en:</strong> Returns {@code null} when the leaf property of a path is missing. For an
     * indefinite path, the missing leaf is represented by a {@code null} element in the result
     * list.
     *
     * <p><strong>zh:</strong> 当路径的叶子属性不存在时返回 {@code null}。对于不确定路径，缺失的叶子属性会在结果列表中表示为 {@code null}
     * 元素。
     */
    DEFAULT_PATH_LEAF_TO_NULL,

    /**
     * <strong>en:</strong> Returns results as a list even when the evaluated path is definite. A
     * scalar result is wrapped in a single-element list. This option is not supported for path
     * functions.
     *
     * <p><strong>zh:</strong> 即使求值路径是确定路径，也始终以列表形式返回结果。标量结果会包装为仅包含一个元素的列表。路径函数不支持此选项。
     */
    ALWAYS_RETURN_LIST,

    /**
     * <strong>en:</strong> Returns the normalized paths of matching nodes instead of their values.
     * The result is always a list of path strings. This option is not supported for path functions.
     *
     * <p><strong>zh:</strong> 返回匹配节点的规范化路径，而不是节点值。结果始终是路径字符串列表。路径函数不支持此选项。
     */
    AS_PATH_LIST,

    /**
     * <strong>en:</strong> Suppresses missing-path errors during path evaluation. A missing
     * definite path returns {@code null}; a missing indefinite path returns an empty list. When
     * combined with {@link #ALWAYS_RETURN_LIST}, a missing result returns an empty list.
     *
     * <p><strong>zh:</strong> 在路径求值过程中抑制路径缺失错误。缺失的确定路径返回 {@code null}，缺失的不确定路径返回空列表。与 {@link
     * #ALWAYS_RETURN_LIST} 组合使用时，缺失结果返回空列表。
     */
    SUPPRESS_EXCEPTIONS,

    /**
     * <strong>en:</strong> Requires every object visited by an indefinite path to contain the
     * properties referenced by that path. A missing required property causes a {@link
     * PathNotFoundException} instead of being skipped.
     *
     * <p><strong>zh:</strong> 要求不确定路径经过的每个对象都包含该路径引用的属性。缺少必需属性时抛出 {@link
     * PathNotFoundException}，而不是跳过该对象。
     */
    REQUIRE_PROPERTIES
}
