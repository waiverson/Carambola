package com.github.waiverson.carambola.support;

import smartrics.rest.client.RestData;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by waiverson on 2016/8/3.
 */
public enum ContentType {

    XML,
    JSON,
    TEXT,
    JS;

    private static Map<String, ContentType> contentTypetoEnum = new HashMap<String, ContentType>();

    static {
        resetDefaultMapping();
    }

    public List<String> toMime() {
        List<String> types = new ArrayList<String>();
        for (Map.Entry<String, ContentType> e: contentTypetoEnum.entrySet()) {
            if (e.getValue().equals(this)) {
                types.add(e.getKey());
            }
        }
        return types;
    }

    public static ContentType typeFor(String t) {
        ContentType r = contentTypetoEnum.get(t);
        if (r == null) {
            r = contentTypetoEnum.get("default");
        }
        return r;
    }

    public static void resetDefaultMapping() {
        contentTypetoEnum.clear();
        contentTypetoEnum.put("default", ContentType.XML);
        contentTypetoEnum.put("application/xml", ContentType.XML);
        contentTypetoEnum.put("application/json", ContentType.JSON);
        contentTypetoEnum.put("text/plain", ContentType.TEXT);
        contentTypetoEnum.put("application/x-javascript", ContentType.JS);
    }

    public static ContentType parse(String contentTypeString) {
        String c = contentTypeString;
        if (c==null) {
            return contentTypetoEnum.get("default");
        }
        int pos = contentTypeString.indexOf(":");
        if(pos > 0) {
            c = contentTypeString.substring(0, pos).trim();
        }
        ContentType ret = contentTypetoEnum.get(c);
        if(ret == null) {
            return contentTypetoEnum.get("default");
        }
        return ret;
    }

    public static void config(Config config) {
        RestData.DEFAULT_ENCODING = config.get(
                "carambola.content.default.charset", Charset.defaultCharset()
                        .name());
        String htmlConfig = config.get("carambola.content.handlers.map", "");
        String configStr = Tools.fromHtml(htmlConfig);
        Map<String, String> map = Tools.convertStringToMap(configStr, "=",
                "\n", true);
        for (Map.Entry<String, String> e : map.entrySet()) {
            String value = e.getValue();
            String enumName = value.toUpperCase();
            ContentType ct = ContentType.valueOf(enumName);
            if (null == ct) {
                ContentType[] values = ContentType.values();
                StringBuffer sb = new StringBuffer();
                sb.append("[");
                for (ContentType cType : values) {
                    sb.append("'").append(cType.toString()).append("' ");
                }
                sb.append("]");
                throw new IllegalArgumentException(
                        "I don't know how to handle " + value + ". Use one of "
                                + values);
            }
            contentTypetoEnum.put(e.getKey(), ct);
        }
    }
}
