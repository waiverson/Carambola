package com.github.waiverson.carambola.support;

import smartrics.rest.client.RestResponse;
import com.github.waiverson.carambola.RunnerVariablesProvider;

/**
 * Handles let expressions to assign constant values to symbols.
 *
 * @author xyc
 *
 */
public class LetBodyConstHandler implements LetHandler {


    @Override
    public String handle(RunnerVariablesProvider variablesProvider,
                         RestResponse response, Object expressionContext, String expression) {
        return expression;
    }

}