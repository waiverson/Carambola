package com.github.waiverson.carambola.support;

import java.util.Collection;

/**
 * Created by xyc 2016/8/16.
 */
public abstract class BodyTypeAdapter extends RestDataTypeAdapter {

    private String charset;

    public BodyTypeAdapter() {super();}

    protected void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCharset() {
        return charset;
    }

    /**
     * Checks if body of a cell is "no-body" meaning empty in the context of a
     * REST call.
     *
     * @param value
     *            the cell
     * @return true if no-body
     */
    protected boolean checkNoBody(final Object value) {
        if (value == null) {
            return true;
        }

        if (value instanceof Collection) {
            return ((Collection<?>) value).size() == 0;
        }
        String s = value.toString();
        return checkNoBodyForString(s);
    }

    private boolean checkNoBodyForString(final String value) {
        return "".equals(value.trim()) || "no-body".equals(value.trim());
    }

    /**
     * @param content
     *            the content of the body response to be XMLified.
     * @return the content as xml.
     */
    public abstract String toXmlString(String content);


    @Override
    public String toString(final Object obj) {
        if (obj == null || obj.toString().trim().equals("")) {
            return "no-body";
        }
        return obj.toString();
    }



}
