package com.github.waiverson.carambola.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by waiverson on 2016/8/15.
 */
public abstract class RestDataTypeAdapter {

    private final List<String> errors = new ArrayList<String>();

    private Object actual;

    private Map<String, String> context;

    public String toString() {
        return toString(get());
    }

    public String toString(Object o) {
        return o == null?"null":(o instanceof String && ((String)o).equals("")?"blank":o.toString());
    }

    public void set(Object a) {
        this.actual = a;
    }

    public Object get() {
        return actual;
    }

    protected void addError(String e) {
        errors.add(e);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void setContext(Map<String, String> c) {this.context = c;}

    protected Map<String, String> getContext() {
        return context;
    }

    public Object fromString(String o) {
        try {
            return o;
        } catch (Exception e) {
            throw new RuntimeException("unable to parse as" + this.getClass().getName() + ": " + o);
        }
    }

}
