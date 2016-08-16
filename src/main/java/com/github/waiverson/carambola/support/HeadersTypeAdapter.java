package com.github.waiverson.carambola.support;

import smartrics.rest.client.RestData.Header;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by waiverson on 2016/8/16.
 */
public class HeadersTypeAdapter extends RestDataTypeAdapter {

    public boolean equals(Object expectedObj, Object actualObj) {
        if (expectedObj == null || actualObj ==null) {
            return false;
        }

        // r1 and r2 are Map<String, String> containing either the header
        // from the HTTP response or the data value in the expected cell
        // equals checks for r1 being a subset of r2
        Collection<Header> expected = (Collection<Header>) expectedObj;
        Collection<Header> actual = (Collection<Header>) actualObj;
        for (Header k : expected) {
            Header aHdr = find(actual, k);
            if (aHdr == null) {
                addError("not found; [" + k.getName() + " : " + k.getValue() + "]");
            }
        }
        return getErrors().size() == 0;
    }

    private Header find(Collection<Header> actual, Header k) {
        for (Header h : actual) {
            boolean nameMatches = h.getName().equals(k.getName());
            boolean valueMatches = Tools.regex(h.getValue(), k.getValue());
            if (nameMatches && valueMatches) {
                return h;
            }
        }
        return null;
    }

    @Override
    public Object parse(String s) throws Exception {
        //parses a cell content as a map of headers;
        // syntax is name:value \n*
        List<Header> expected = new ArrayList<Header>();
        if (!"".equals(s.trim())) {
            String expstr = Tools.fromHtml((s.trim()));
            String[] nvpArray = expstr.split("\n");
            for (String nvp : nvpArray) {
                try {
                    String[] nvpEl = nvp.split(":", 2);
                    expected.add(new Header(nvpEl[0].trim(), nvpEl[1].trim()));
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException("Each entry in the must be separated by \\n and each entry must be expressed as a name:value")
                }
            }
        }
        return expected;
    }

    @Override
    public String toString(Object obj) {
        StringBuffer sb = new StringBuffer();
        List<Header> list = (List<Header>) obj;
        for (Header h : list) {
            sb.append(h.getName()).append(" : ").append(h.getValue()).append("\n");
        }
        return sb.toString().trim();
    }
}
