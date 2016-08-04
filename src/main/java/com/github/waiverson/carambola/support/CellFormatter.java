package com.github.waiverson.carambola.support;

/**
 * Created by waiverson on 2016/8/4.
 */
public interface CellFormatter<E> {

    void exception(CellWrapper<E> cellWrapper, Throwable exception);
    void exception(CellWrapper<E> cellWrapper, String exceptionMessage);
    void check(CellWrapper<E> valueCell, RestDataTypeAdapter adapter);
    String label(String string);
    void setDisplayActual(boolean displayActual);
    void setDisplayAbsoluteURLInFull(boolean displayAbsoluteURLInFull);
    void setMinLengthForToggleCollapse(int minLength);
}
