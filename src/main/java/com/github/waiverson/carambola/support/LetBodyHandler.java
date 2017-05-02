package com.github.waiverson.carambola.support;

import java.util.Map;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import smartrics.rest.client.RestResponse;
import smartrics.rest.fitnesse.fixture.RunnerVariablesProvider;

/**
 * Handles body of the last response on behalf of LET in RestFixture.
 *
 * @author xyc
 *
 */
public class LetBodyHandler implements LetHandler {

    @Override
    public String handle(RunnerVariablesProvider variablesProvider,
                         RestResponse response, Object expressionContext, String expression) {
        @SuppressWarnings("unchecked")
        Map<String, String> namespaceContext = (Map<String, String>) expressionContext;
        String contentTypeString = response.getContentType();
        String charset = response.getCharset();
        ContentType contentType = ContentType.parse(contentTypeString);
        BodyTypeAdapter bodyTypeAdapter = new BodyTypeAdapterFactory(variablesProvider)
                .getBodyTypeAdapter(contentType, charset);
        String body = bodyTypeAdapter.toXmlString(response.getBody());
        if (body == null) {
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
            // ignore - may be that it's evaluating to a string
            val = (String) Tools.extractXPath(namespaceContext, expression, body, XPathConstants.STRING, charset);
        }
        if (val != null) {
            val = val.trim();
        }
        return val;
    }
}
