package com.github.waiverson.carambola;

import com.github.waiverson.carambola.support.CellWrapper;

/**
 * Created by Administrator on 2016/8/4.
 */
public class DslCell implements CellWrapper<String>{

    private String cell;

    /**
     * a slim cell.
     * @param c the content.
     */
    public DslCell(String c) {
        this.cell = c;
    }


    public String text() {
        return cell;
    }


    public void body(String string) {
        cell = string;
    }


    public String body() {
        return cell;
    }

    public void addToBody(String string) {
        cell = cell + string;
    }

    public String getWrapped() {
        return cell;
    }
}
