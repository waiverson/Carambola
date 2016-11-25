package com.github.waiverson.carambola.support;

import com.github.waiverson.carambola.RunnerVariablesProvider;
import smartrics.rest.client.RestResponse;

/**
 * Created by waiverson on 16/11/25.
 */

public class LetBodyJsHandler implements LetHandler {

	public String handle(RunnerVariablesProvider variablesProvider,
								RestResponse response, Object expressionContext, String expression) {
		JavascriptWrapper js = new JavascriptWrapper(variablesProvider);
		Object result  = js.evaluateExpression(response, expression);
	}
}
