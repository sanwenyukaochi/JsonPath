package com.jayway.jsonpath;

public interface EvaluationListener {

    EvaluationContinuation resultFound(FoundResult found);

    enum EvaluationContinuation {
        CONTINUE,

        ABORT
    }

    interface FoundResult {

        int index();

        String path();

        Object result();
    }
}
