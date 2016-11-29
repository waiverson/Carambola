package com.github.waiverson.carambola.support;

import com.github.waiverson.carambola.RunnerVariablesProvider;
import org.w3c.dom.NodeList;
import smartrics.rest.client.RestResponse;

import java.util.Map;

/**
 * Created by waiverson on 16/11/25.
 */


public class LetBodyXmlHandler implements LetHandler {

	public String handle(RunnerVariablesProvider variablesProvider,
						 RestResponse response, Object expressionContext, String expression) {

		Map<String, String> namespaceContext = (Map<String, String>)expressionContext;
		NodeList list = Tools.extractXPath(namespaceContext, expression, response.getBody());
		String val = Tools.xPathResultToXmlString(list);
		int pos = val.indexOf("?>");
		if (pos >= 0) {
			val = val.substring(pos + 2);
		}
		return val;

	}
}
