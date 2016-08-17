package com.github.waiverson.carambola;

import com.github.waiverson.carambola.support.CellFormatter;
import com.github.waiverson.carambola.support.CellWrapper;
import com.github.waiverson.carambola.support.RestDataTypeAdapter;
import com.github.waiverson.carambola.support.Tools;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Created by Administrator on 2016/8/17.
 */
public class DslFormatter implements CellFormatter<String> {

    private int minLenForToggle = -1;
    private boolean displayActual;
    private boolean displayAbsoluteURLInFull;

    public DslFormatter() {

    }


    public void setDisplayActual(boolean d) {
        this.displayActual = d;
    }


    public void setDisplayAbsoluteURLInFull(boolean displayAbsoluteURLInFull) {
        this.displayAbsoluteURLInFull = displayAbsoluteURLInFull;
    }


    public void setMinLengthForToggleCollapse(int minLen) {
        this.minLenForToggle = minLen;
    }

    public boolean isDisplayActual() {
        return displayActual;
    }

    public void exception(CellWrapper<String> cell, String exceptionMessage) {
        cell.body("error: " + Tools.wrapInDiv(exceptionMessage));
    }

    public void exception(CellWrapper<String> cell, Throwable exception) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        exception.printStackTrace(ps);
        String m = Tools.toHtml(cell.getWrapped() + "\n----\n") + Tools.toCode(Tools.toHtml(out.toString()));
        cell.body("error:" + Tools.wrapInDiv(m));
    }

    public void check(CellWrapper<String> expected, RestDataTypeAdapter actual) {
        if (null == expected.body() || "".equals(expected.body())) {
            if (actual.get() == null) {
                return;
            } else {
                expected.body(gray(actual.get().toString()));
                return;
            }
        }

        if (actual.get() != null && actual.equals(expected.body(), actual.get().toString())) {
            right(expected, actual);
        } else {
            wrong(expected, actual);
        }
    }

    public String label(String string) {
        return Tools.toHtmlLabel(string);
    }

    public void wrong(CellWrapper<String> expected, RestDataTypeAdapter ta) {
        String expectedContent = expected.body();
        expected.body(Tools.makeContentForWrongCell(expectedContent, ta, this, minLenForToggle));
        expected.body("fail: " + Tools.wrapInDiv(expected.body()));
    }

    public void right(CellWrapper<String> expected, RestDataTypeAdapter ta) {
        expected.body("pass:" + Tools.wrapInDiv(Tools.makeContentForRightCell(
                expected.body(), ta, this, minLenForToggle
        )));
    }

    public String gray(String string) {
        return "report: " + Tools.wrapInDiv(Tools.toHtml(string));
    }

    public void asLink(CellWrapper<String> cell, String resolvedUrl, String link, String text) {
        String actualText = text;
        String parsed = null;
        if (displayAbsoluteURLInFull) {
            parsed = Tools.fromSimpleTag(resolvedUrl);
            if(parsed.trim().startsWith("http")) {
                actualText = parsed;
            }
        }
        System.out.println("displayAbsoluteURLInFull: '" + displayAbsoluteURLInFull
                + "', cellText: '" + cell.text()
                + "', resolvedUrl: '" + resolvedUrl
                + "', parsed: '" + parsed
                + "', actualText: '" + actualText + "'");
        cell.body("report:" + Tools.wrapInDiv(Tools.toHtmlLink(link, actualText)));
    }

    public String fromRaw(String text) {
        return Tools.fromHtml(text);
    }

}
