package com.github.waiverson.carambola.support;

import java.util.List;
import com.github.waiverson.carambola.RunnerVariablesProvider;

/**
 * Created by waiverson on 2016/8/19.
 */
public class JSONBodyTypeAdapter extends XPathBodyTypeAdapter {
    private boolean forceJsEvaluation = false;
    private final JavascriptWrapper wrapper;

    /**
     * def ctor
     * @param variablesProvider
     */
    public JSONBodyTypeAdapter(RunnerVariablesProvider variablesProvider) {
        wrapper = new JavascriptWrapper(variablesProvider);
    }

    @Override
    protected boolean eval(String expr, String json) {
        // for backward compatibility we should keep for now xpath expectations
        if (!forceJsEvaluation && Tools.isValidXPath(getContext(), expr) && !wrapper.looksLikeAJsExpression(expr)) {
            throw new IllegalArgumentException("XPath expectations in JSON content are not supported anymore. Please use JavaScript expressions.");
        }
        Object exprResult = wrapper.evaluateExpression(json, expr);
        if (exprResult == null) {
            return false;
        }
        return Boolean.parseBoolean(exprResult.toString());
    }

    @Override
    public Object parse(String possibleJsContent) throws Exception {
        if (possibleJsContent == null || possibleJsContent.trim().indexOf("/* javascript */") < 0) {
            forceJsEvaluation = false;
            return super.parse(possibleJsContent);
        }
        forceJsEvaluation = true;
        String content = Tools.fromHtml(possibleJsContent.trim());
        return content;
    }

    @Override
    public boolean equals(Object expected, Object actual) {
        if (checkNoBody(expected)) {
            return checkNoBody(actual);
        }
        if (checkNoBody(actual)) {
            return checkNoBody(expected);
        }
        if (expected instanceof List<?>) {
            return super.equals(expected, actual);
        }
        boolean result = false;
        if (expected instanceof String) {
            result = eval(expected.toString(), actual.toString());
            if (!result) {
                addError("not found: '" + expected.toString() + "'");
            }
        }
        return result;
    }

    @Override
    public String toString(Object obj) {
        if (obj == null || obj.toString().trim().equals("")) {
            return "no-body";
        }
        // the actual value is passed as an xml string
        // TODO: pretty print?
        return obj.toString();
    }

    @Override
    public String toXmlString(String content) {
        return Tools.fromJSONtoXML(content);
    }

}
