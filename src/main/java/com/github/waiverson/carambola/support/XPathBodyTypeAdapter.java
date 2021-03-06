package com.github.waiverson.carambola.support;

import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.util.ArrayList;
import java.util.List;

/**
 * type adapter for body cells with xml content
 * Created by waiverson on 2016/8/19.
 */
public class XPathBodyTypeAdapter extends BodyTypeAdapter {



     @Override
    public boolean equals(final Object expected, final Object actual) {
        if (checkNoBody(expected)) {
            return checkNoBody(actual);
        }
        if (checkNoBody(actual)) {
            return checkNoBody(expected);
        }

        List<String> expressions = (List<String>) expected;
        for (String expr : expressions) {
            try {
                boolean b = eval(expr, actual.toString());
                if (!b) {
                    addError("not found: '" + expr + "'");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot evaluate'" + expr
                        + "' in " + actual.toString(),e );
            }
        }
        return getErrors().size() == 0;
    }

    protected boolean eval(String expr, String content) {
        Boolean b;
        try {
            NodeList ret = Tools.extractXPath(getContext(), expr, content, getCharset());
            return !(ret == null || ret.getLength() == 0);
        } catch (IllegalArgumentException e) {
            b = (Boolean)Tools.extractXPath(getContext(), expr, content, XPathConstants.BOOLEAN, getCharset());
        }
        return b;
    }

    @Override
    public Object parse(String expectedListOfXPathsAsString) throws Exception {
        List<String> expectedXPathAsList = new ArrayList<String>();
        if (expectedListOfXPathsAsString == null) {
            return expectedXPathAsList;
        }
        String expStr = expectedListOfXPathsAsString.trim();
        if ("no-body".equals(expStr)) {
            return expectedXPathAsList;
        }
        if ("".equals(expStr)) {
            return expectedXPathAsList;
        }
        expStr = Tools.fromHtml(expStr);
        String[] nvpArray = expStr.split("\n");
        for (String nvp : nvpArray) {
            if (!"".equals(nvp.trim())) {
                expectedXPathAsList.add(nvp.trim());
            }
        }
        return expectedXPathAsList;
    }

    @Override
    public String toXmlString(String content) {
        return content;
    }

}
