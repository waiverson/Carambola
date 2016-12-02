package com.github.waiverson.carambola.support;

import com.github.waiverson.carambola.RunnerVariablesProvider;
import smartrics.rest.client.RestResponse;

/**
 * handles let expression to assign constant values to symbols;
 *
 * Created by waiverson on 16/11/25.
 */

public class LetBodyConstHandler implements LetHandler {

	public String handle(RunnerVariablesProvider variablesProvider,
						 RestResponse response, Object expressionContext, String expression) {
		return expression;
	}
}
