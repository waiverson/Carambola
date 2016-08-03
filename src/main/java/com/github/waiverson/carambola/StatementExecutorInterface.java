package com.github.waiverson.carambola;

import com.github.waiverson.carambola.InstructionExecutor;

/**
 * Created by waiverson on 2016/8/3.
 */

public interface StatementExecutorInterface extends InstructionExecutor{
    Object getSymbol(String var1);

    Object getInstance(String var1);

    boolean stopHashBeenRequested();

    void reset();

    void setInstance(String var1, Object var2);
}
