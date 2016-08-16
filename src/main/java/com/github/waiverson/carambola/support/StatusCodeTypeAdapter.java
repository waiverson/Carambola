package com.github.waiverson.carambola.support;

/**
 * Created by waiverson on 2016/8/16.
 */
public class StatusCodeTypeAdapter extends RestDataTypeAdapter {

    public boolean equals(Object r1, Object r2) {
        if (r1 == null || r2 == null) {
            return false;
        }

        String expected = r1.toString();
        String actual = (String) r2;
        if (!Tools.regex(actual, expected)) {
            addError("not match: " + expected);
        }
        return getErrors().size() == 0;
    }

    public Object parse(String s) {
        if (s ==null) {
            return "null";
        }
        return s.trim();
    }

    public String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj.toString().trim().equals("")) {
            return "blank";
        }
        return obj.toString();
    }
}
