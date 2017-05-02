package com.github.waiverson.carambola.support;

import com.github.waiverson.carambola.StatementExecutorInterface;

/**
 * Created by xyc on 2016/8/3.
 */

public class DslVariables extends Variables{

    private final StatementExecutorInterface executor;

    /**
     * initialises the variables. reade
     * {@code Carambola.null.value.representation} to know how to render
     * {@code null}s.
     *
     * @param c
     * @param executor
     */

    public DslVariables() { super(); }

    public DslVariables(Config c, StatementExecutorInterface executor) {
        super(c);
        this.executor = executor;
    }

    public void put(String label, String val) {
        executor.assign(label, val);
    }

    public String get(String label) {
        String result = executor.getSymbol(label).toString();
        return result;
    }
}
