package com.github.waiverson.carambola.support;

import smartrics.rest.client.RestResponse;
import com.github.waiverson.carambola.RunnerVariablesProvider;

/**
 * Strategy to handle LET expressions.
 *
 * @author xyc
 *
 */
public interface LetHandler {

    /**
     *
     * @param variablesProvider
     * @param response
     *            the http response
     * @param expressionContext
     *            the json expression context
     * @param expression
     *            the expression
     * @return applies the expression to response within the given context.
     */
    String handle(RunnerVariablesProvider variablesProvider,
                  RestResponse response, Object expressionContext, String expression);

}
