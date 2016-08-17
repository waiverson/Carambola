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

    /**
     * formats a cell representing a wrong expectation.
     *
     * @param expected
     *            the expected value
     * @param typeAdapter
     *            the adapter with the actual value.
     */
    void wrong(CellWrapper<E> expected, RestDataTypeAdapter typeAdapter);

    /**
     * formats a cell representing a right expectation.
     *
     * @param expected
     *            the expected value
     * @param typeAdapter
     *            the adapter with the actual value.
     */
    void right(CellWrapper<E> expected, RestDataTypeAdapter typeAdapter);

    /**
     * formats a cell with a gray background. used to ignore the content or for
     * comments.
     *
     * @param string
     *            the content
     * @return the content grayed out.
     */
    String gray(String string);

    /**
     * formats the content as a hyperlink.
     *
     * @param cell
     *            the cell.
     * @param resolvedUrl
     * 	          the cell content after symbols' substitution.
     * @param link
     *            the uri in the href.
     * @param text
     *            the text.
     */
    void asLink(CellWrapper<E> cell, String resolvedUrl, String link, String text);

    /**
     * @return true if actual values are rendered.
     */
    boolean isDisplayActual();

    /**
     * in SLIM cell content is HTML escaped - we abstract this method to
     * delegate to formatter the cleaning of the content.
     *
     * @param text the text
     * @return the cleaned text
     */
    String fromRaw(String text);
}
