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

    /**
     * represents xml content.
     */
    XML,
    /**
     * represents json content.
     */
    JSON,
    /**
     * represents plain text content.
     */
    TEXT,
    /**
     * represents javascript content.
     */
    JS;

    private static Map<String, ContentType> contentTypeToEnum = new HashMap<String, ContentType>();

    static {
        resetDefaultMapping();
    }

    /**
     * @return the content type as mime type
     */
    public List<String> toMime() {
        List<String> types = new ArrayList<String>();
        for (Map.Entry<String, ContentType> e : contentTypeToEnum.entrySet()) {
            if (e.getValue().equals(this)) {
                types.add(e.getKey());
            }
        }
        return types;
    }

    /**
     * @param t
     *            the content type string
     * @return the registered content type matching the input string.
     */
    public static ContentType typeFor(String t) {
        ContentType r = contentTypeToEnum.get(t);
        if (r == null) {
            r = contentTypeToEnum.get("default");
        }
        return r;
    }

    /**
     * configures the internal map of handled content types (See
     * {@link RestFixtureConfig}). It reads two properties:
     * <ul>
     * <li>{@code restfixture.content.default.charset} to determine the default
     * charset for that content type, in cases when the content type header of
     * the request/response isn't specifying the charset. If this config
     * parameter is not set, then the default value is
     * {@link Charset#defaultCharset()}.
     * <li>{@code restfixture.content.handlers.map} to override the default map.
     * </ul>
     *
     * @param config
     *            the config
     */
    public static void config(Config config) {
        RestData.DEFAULT_ENCODING = config.get(
                "restfixture.content.default.charset", Charset.defaultCharset()
                        .name());
        String htmlConfig = config.get("restfixture.content.handlers.map", "");
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
            contentTypeToEnum.put(e.getKey(), ct);
        }
    }

    /**
     * resets the internal cache to default values.
     * <table border="1">
     * <tr>
     * <td>{@code default}</td>
     * <td>{@link ContentType#XML}</td>
     * </tr>
     * <tr>
     * <td>{@code application/xml}</td>
     * <td>{@link ContentType#XML}</td>
     * </tr>
     * <tr>
     * <td>{@code application/json}</td>
     * <td>{@link ContentType#JSON}</td>
     * </tr>
     * <tr>
     * <td>{@code text/plain}</td>
     * <td>{@link ContentType#TEXT}</td>
     * </tr>
     * <tr>
     * <td>{@code application/x-javascript}</td>
     * <td>{@link ContentType#JS}</td>
     * </tr>
     * </table>
     */
    public static void resetDefaultMapping() {
        contentTypeToEnum.clear();
        contentTypeToEnum.put("default", ContentType.XML);
        contentTypeToEnum.put("application/xml", ContentType.XML);
        contentTypeToEnum.put("application/json", ContentType.JSON);
        contentTypeToEnum.put("text/plain", ContentType.TEXT);
        contentTypeToEnum.put("application/x-javascript", ContentType.JS);
    }

    /**
     * parses a string to a content type.
     * @param contentTypeString
     * @return the {@link ContentType}.
     */
    public static ContentType parse(String contentTypeString) {
        String c = contentTypeString;
        if (c == null) {
            return contentTypeToEnum.get("default");
        }
        int pos = contentTypeString.indexOf(";");
        if (pos > 0) {
            c = contentTypeString.substring(0, pos).trim();
        }
        ContentType ret = contentTypeToEnum.get(c);
        if (ret == null) {
            return contentTypeToEnum.get("default");
        }
        return ret;
    }
}

