package com.jayway.jsonpath;

public interface EvaluationListener {

    EvaluationContinuation resultFound(FoundResult found);

    public static enum EvaluationContinuation {
        CONTINUE,

        ABORT
    }

    public interface FoundResult {

        int index();

        String path();

        Object result();
    }
}
