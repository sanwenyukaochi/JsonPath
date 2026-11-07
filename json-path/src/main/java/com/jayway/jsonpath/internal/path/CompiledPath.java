package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.internal.EvaluationAbortException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.ParamType;
import com.jayway.jsonpath.internal.function.Parameter;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompiledPath implements Path {

    private static final Logger logger = LoggerFactory.getLogger(CompiledPath.class);

    private final RootPathToken root;

    private final boolean isRootPath;

    public CompiledPath(RootPathToken root, boolean isRootPath) {
        this.root = invertScannerFunctionRelationship(root);
        this.isRootPath = isRootPath;
    }

    @Override
    public boolean isRootPath() {
        return isRootPath;
    }

    private RootPathToken invertScannerFunctionRelationship(final RootPathToken path) {
        if (path.isFunctionPath() && path.next() instanceof ScanPathToken) {
            PathToken token = path;
            PathToken prior = null;
            while (null != (token = token.next()) && !(token instanceof FunctionPathToken)) {
                prior = token;
            }

            if (token instanceof FunctionPathToken) {
                prior.setNext(null);
                path.setTail(prior);

                Parameter parameter = new Parameter();
                parameter.setPath(new CompiledPath(path, true));
                parameter.setType(ParamType.PATH);
                ((FunctionPathToken) token).setParameters(Arrays.asList(parameter));
                RootPathToken functionRoot = new RootPathToken('$');
                functionRoot.setTail(token);
                functionRoot.setNext(token);

                return functionRoot;
            }
        }
        return path;
    }

    @Override
    public EvaluationContext evaluate(
            Object document, Object rootDocument, Configuration configuration, boolean forUpdate) {
        if (logger.isDebugEnabled()) {
            logger.debug("Evaluating path: {}", toString());
        }

        EvaluationContextImpl ctx =
                new EvaluationContextImpl(this, rootDocument, configuration, forUpdate);
        try {
            PathRef op = ctx.forUpdate() ? PathRef.createRoot(rootDocument) : PathRef.NO_OP;
            root.evaluate("", op, document, ctx);
        } catch (EvaluationAbortException abort) {
        }

        return ctx;
    }

    @Override
    public EvaluationContext evaluate(
            Object document, Object rootDocument, Configuration configuration) {
        return evaluate(document, rootDocument, configuration, false);
    }

    @Override
    public boolean isDefinite() {
        return root.isPathDefinite();
    }

    @Override
    public boolean isFunctionPath() {
        return root.isFunctionPath();
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public RootPathToken getRoot() {
        return root;
    }
}
