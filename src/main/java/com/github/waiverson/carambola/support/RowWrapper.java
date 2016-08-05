package com.github.waiverson.carambola.support;

/**
 * Created by waievrson on 2016/8/4.
 */
public interface RowWrapper<E> {

    /**
     * @param c
     * @return the {@link CellWrapper} at a given position
     */
    CellWrapper<E> getCell(int c);

    /**
     * @return the row size.
     */
    int size();

    /**
     * removes a cell at a given position.
     *
     * @param c
     * @return the removed cell.
     */
    CellWrapper<E> removeCell(int c);
}
