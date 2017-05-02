package com.github.waiverson.carambola.support;

import com.github.waiverson.carambola.StatementExecutorInterface;

/**
 * Facade to FitNesse global symbols map for SliM.
 *
 * @author xyc
 */
public class DslVariables extends Variables {

    private final StatementExecutorInterface executor;

    /**
     * initialises the variables. reade
     * {@code restfixture.null.value.representation} to know how to render
     * {@code null}s.
     *
     * @param c
     * @param executor
     */
    public DslVariables(Config c, StatementExecutorInterface executor) {
        super(c);
        this.executor = executor;
    }

    /**
     * puts a value.
     *
     * @param label
     * @param val
     */
    public void put(String label, String val) {
        executor.assign(label, val);
    }

    /**
     * gets a value.
     *
     * @param label
     * @return the value.
     */
    public String get(String label) {
        String result = executor.getSymbol(label).toString();
        return result;
    }

}
