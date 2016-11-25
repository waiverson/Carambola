package com.github.waiverson.carambola.support;

import com.github.waiverson.carambola.RunnerVariablesProvider;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import smartrics.rest.client.RestResponse;

import javax.xml.xpath.XPathConstants;
import java.util.Map;

/**
 * Created by waiverson on 16/11/25.
 */

public class LetBodyHandler implements LetHandler {

	public String handle(RunnerVariablesProvider variablesProvider,
			RestResponse response, Object expressionContext, String expression) {

		Map<String, String> namespaceContext = (Map<String, String>)expressionContext;
		String contentTypeString = response.getContentType();
		String charset = response.getCharset();
		ContentType contentType = ContentType.parse(contentTypeString);
		BodyTypeAdapter bodyTypeAdapter = new BodyTypeAdapterFactory(variablesProvider).getBodyTypeAdapter(contentType, charset);
		String body = bodyTypeAdapter.toXmlString(response.getBody());
		if (body ==null) {
			return null;
		}
		String val = null;
		try {
			NodeList list = Tools.extractXPath(namespaceContext, expression, body);
			Node item = list.item(0);
			if (item != null) {
				val = item.getTextContent();
			}
		} catch (IllegalArgumentException e) {
			val = (String)Tools.extractXPath(namespaceContext, expression, body, XPathConstants.STRING, charset);
		}

		if (val != null) {
			val = val.trim();
		}
		return val;

	}


}
