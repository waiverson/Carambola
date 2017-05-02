package com.github.waiverson.carambola.support;

/**
 * Created by waiverson on 2016/8/4.
 */
public interface CellFormatter<E> {

    /**
     * formats a cell containing an exception.
     *
     * @param cellWrapper
     *            the cell wrapper
     * @param exception
     *            the excteption to render.
     */
    void exception(CellWrapper<E> cellWrapper, Throwable exception);

    /**
     * formats a cell containing an exception.
     *
     * @param cellWrapper
     *            the cell wrapper
     * @param exceptionMessage
     *            the exception message to render.
     */
    void exception(CellWrapper<E> cellWrapper, String exceptionMessage);

    /**
     * formats a check cell.
     *
     * @param valueCell
     *            the cell value.
     * @param adapter
     *            the adapter interpreting the value.
     */
    void check(CellWrapper<E> valueCell, RestDataTypeAdapter adapter);

    /**
     * formats a cell label
     *
     * @param string
     *            the label
     * @return the cell content as a label.
     */
    String label(String string);

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
     * sets whether the cell should display the actual value after evaluation.
     *
     * @param displayActual
     *            true if actual value has to be rendered.
     */
    void setDisplayActual(boolean displayActual);

    /**
     * sets whether absolute urls are displayed in full
     *
     * @param displayAbsoluteURLInFull
     */
    void setDisplayAbsoluteURLInFull(boolean displayAbsoluteURLInFull);

    /**
     * renders the cell as a toggle area if the content of the cell is over the
     * min value set here.
     *
     * @param minLen
     *            the min value of the content of a cell.
     */
    void setMinLengthForToggleCollapse(int minLen);

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
