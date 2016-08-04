package com.github.waiverson.carambola.support;

/**
 * Created by waiverson on 2016/8/4.
 */
public interface CellWrapper<E> {

    /**
     *
     * @return the underlying cell object.
     */
    E getWrapped();

    /**
     * @return the text in the cell.
     */
    String text();


    /**
     * @param string
     *            the body of the cell to set.
     */
    void body(String string);

    /**
     * @return the current body of the cell.
     */
    String body();

    /**
     * appends to the current cell body.
     *
     * @param string
     *            the string to append.
     */
    void addToBody(String string);
}
