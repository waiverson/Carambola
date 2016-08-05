package com.github.waiverson.carambola;


import com.github.waiverson.carambola.support.CellWrapper;
import com.github.waiverson.carambola.support.RowWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/4.
 */
public class DslRow implements RowWrapper<String> {

    private final List<CellWrapper<String>> row;

    public DslRow(List<String> rawRow) {
        this.row = new ArrayList<CellWrapper<String>>();
        for (String r: rawRow) {
            this.row.add(new DslCell(r));
        }
    }

    public CellWrapper<String> getCell(int c) {
        if (c < this.row.size()) {
            return this.row.get(c);
        }
        return null;
    }

    public int size() {
        if (row !=null) {
            return row.size();
        }
        return 0;
    }

    public List<String> asList() {
        List<String> ret = new ArrayList<String>();
        for (CellWrapper<String> w :row) {
            ret.add(w.body());
        }
        return ret;
    }

    public CellWrapper<String> removeCell(int c) {
        if (c < this.row.size()) {
            return this.row.remove(c);
        }
        return null;
    }


}
