package com.github.waiverson.carambola;

import com.github.waiverson.carambola.support.CellFormatter;
import com.github.waiverson.carambola.support.CellWrapper;
import com.github.waiverson.carambola.support.RestDataTypeAdapter;
import com.github.waiverson.carambola.support.Tools;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Formatter of cells handled by Slim.
 *
 * @author xyc
 *
 */
public class DslFormatter implements CellFormatter<String> {

    private int minLenForToggle = -1;
    private boolean displayActual;
    private boolean displayAbsoluteURLInFull;

    public DslFormatter() {
    }

    @Override
    public void setDisplayActual(boolean d) {
        this.displayActual = d;
    }

    @Override
    public void setDisplayAbsoluteURLInFull(boolean displayAbsoluteURLInFull) {
        this.displayAbsoluteURLInFull = displayAbsoluteURLInFull;
    }


    @Override
    public void setMinLengthForToggleCollapse(int minLen) {
        this.minLenForToggle = minLen;
    }

    public boolean isDisplayActual() {
        return displayActual;
    }

    @Override
    public void exception(CellWrapper<String> cell, String exceptionMessage) {
        cell.body("error:" + Tools.wrapInDiv(exceptionMessage));
    }

    @Override
    public void exception(CellWrapper<String> cell, Throwable exception) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        exception.printStackTrace(ps);
        //String m = Tools.toHtml(cell.getWrapped() + "\n-----\n") + Tools.toCode(Tools.toHtml(out.toString()));
        String m = Tools.toHtml(cell.getWrapped() + "\n-----\n") + Tools.toCode(Tools.toHtml(out.toString()));
        cell.body("error:" + Tools.wrapInDiv(m));
        //cell.body("error:" + m);
    }

    @Override
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

    @Override
    public String label(String string) {
        return Tools.toHtmlLabel(string);
    }

    @Override
    public void wrong(CellWrapper<String> expected, RestDataTypeAdapter ta) {
        String expectedContent = expected.body();
        expected.body(Tools.makeContentForWrongCell(expectedContent, ta, this, minLenForToggle));
        expected.body("fail:" + Tools.wrapInDiv(expected.body()));
    }

    @Override
    public void right(CellWrapper<String> expected, RestDataTypeAdapter typeAdapter) {
        expected.body("pass:" + Tools.wrapInDiv(Tools.makeContentForRightCell(expected.body(), typeAdapter, this, minLenForToggle)));
    }

    @Override
    public String gray(String string) {
        return "report:" + Tools.wrapInDiv(Tools.toHtml(string));
    }

    @Override
    public void asLink(CellWrapper<String> cell, String resolvedUrl, String link, String text) {
        String actualText = text;
        String parsed = null;
        if(displayAbsoluteURLInFull) {
            parsed = Tools.fromSimpleTag(resolvedUrl);
            if(parsed.trim().startsWith("http")) {
                actualText = parsed;
            }
        }
        System.out.println("displayAbsoluteURLInFull: '" + displayAbsoluteURLInFull + "', cellText: '" + cell.text() + "', resolvedUrl: '" + resolvedUrl + "', parsed: '" + parsed + "', actualText: '" + actualText + "'");
        cell.body("report:" + Tools.wrapInDiv(Tools.toHtmlLink(link, actualText)));
    }

    @Override
    public String fromRaw(String text) {
        return Tools.fromHtml(text);
    }

}

