package com.github.waiverson.carambola.support;

import smartrics.rest.client.RestResponse;
import com.github.waiverson.carambola.RunnerVariablesProvider;

/**
 * Handles let expressions on XML content, returning XML string rather than the
 * string with the content within the tags.
 *
 * @author xyc
 *
 */
public class LetBodyJsHandler implements LetHandler {

    @Override
    public String handle(RunnerVariablesProvider variablesProvider,
                         RestResponse response, Object expressionContext, String expression) {
        JavascriptWrapper js = new JavascriptWrapper(variablesProvider);
        Object result = js.evaluateExpression(response, expression);
        if (result == null) {
            return null;
        }
        return result.toString();
    }


}