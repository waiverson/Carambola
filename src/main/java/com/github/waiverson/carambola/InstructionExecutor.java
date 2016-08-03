package com.github.waiverson.carambola;

/**
 * Created by waiverson on 2016/8/3.
 */
public interface InstructionExecutor {
    void assign(String var1, Object var2);

    void addPath(String var1);

    void create(String var1, String var2, Object... var3);

    Object callAndAssign(String var1, String var2, String var3, Object... var4);

    Object call(String var1, String var2, Object... var3);

}
