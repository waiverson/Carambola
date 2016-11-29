package com.github.waiverson.carambola.support;

import com.github.waiverson.carambola.RunnerVariablesProvider;
import org.mozilla.javascript.*;
import smartrics.rest.client.RestResponse;



/**
 * Created by waiverson on 16/11/25.
 */


public class JavascriptWrapper {


	public static final String SYMBOLS_OBJ_NAME = "symbols";
	public static final String JSON_OBJ_NAME = "jsonbody";
	private RunnerVariablesProvider variablesProvider;

	public JavascriptWrapper(RunnerVariablesProvider variablesProvider) {
		this.variablesProvider = variablesProvider;
	}

	// evaluates an expression on a given json object represented as string
	public  Object evaluateExpression(RestResponse response, String expression) {
		if (expression == null) {
			return null;
		}

		Context context = Context.enter();
		ScriptableObject scope = context.initStandardObjects();
		injectCarambolaSymbolMap(scope);
		injectJson(context, scope, json);
		Object result = evaluateExpression(context, scope, expression);
		return result;

	}

	private Object evaluateExpression(Context cx, ScriptableObject scope, String expression){
		try {
			Object result = cx.evaluateString(scope, expression, null, 1, null);
			return result;
		} catch (EvaluatorException e) {
			throw new JavaScriptException(e.getMessage());
		} catch (EcmaError e) {
			throw new JavaScriptException(e.getMessage());
		}

	}

	private void injectCarambolaSymbolMap(ScriptableObject scope) {
		Variables v = variablesProvider.createRunnerVariables();
		Object wrappedVariables = Context.javaToJS(v, scope);
		ScriptableObject.putProperty(scope, SYMBOLS_OBJ_NAME, wrappedVariables);
	}

	private void injectJson(Context cx, ScriptableObject scope, String json) {
		evaluateExpression(cx, scope, "var " + JSON_OBJ_NAME + "=" + json);
	}


}
