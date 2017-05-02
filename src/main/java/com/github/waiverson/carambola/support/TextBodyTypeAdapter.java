package com.github.waiverson.carambola.support;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;


/**
 * Type adapter for body cell containing plain text.
 *
 * @author smartrics
 *
 */
public class TextBodyTypeAdapter extends BodyTypeAdapter {

    @Override
    public boolean equals(Object exp, Object act) {
        if (exp == null || act == null) {
            return false;
        }
        String expected = exp.toString();

        String actual = (String) act;
        try {
            Pattern p = Pattern.compile(expected);
            Matcher m = p.matcher(actual);
            if (!m.matches() && !m.find()) {
                addError("no regex match: " + expected);
            }
        } catch (PatternSyntaxException e) {
            // lets try to string match just to be kind
            if (!expected.equals(actual)) {
                addError("no string match found: " + expected);
            }
        }
        return getErrors().size() == 0;
    }

    @Override
    public Object parse(String s) {
        if (s == null) {
            return "null";
        }
        return s.trim();
    }

    @Override
    public String toXmlString(String content) {
        return "<text>" + content + "</text>";
    }

}
