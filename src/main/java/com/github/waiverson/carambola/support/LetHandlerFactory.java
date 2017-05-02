package com.github.waiverson.carambola.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds strategies to handle LET body.
 *
 * Supported strategies:
 * <table>
 * <tr>
 * <td>{@code header}</td><td>applies the expression to the response headers</td>
 * </tr>
 * <tr>
 * <td>{@code body}</td><td>applies the expression to the body</td>
 * </tr>
 * <tr>
 * <td>{@code body:xml}</td><td>applies the expression to the body as XML. expressions are XPaths.</td>
 * </tr>
 * <tr>
 * <td>{@code js}</td><td>applies expression to body as JSON</td>
 * </tr>
 * <tr>
 * <td>{@code const}</td><td>it's actually  a shortcut to allow setting of const labels</td>
 * </tr>
 * </table>
 *
 * @author xyc
 *
 */
public class LetHandlerFactory {
    private static Map<String, LetHandler> strategies = new HashMap<String, LetHandler>();

    static {
        strategies.put("header", new LetHeaderHandler());
        strategies.put("body", new LetBodyHandler());
        strategies.put("body:xml", new LetBodyXmlHandler());
        strategies.put("js", new LetBodyJsHandler());
        strategies.put("const", new LetBodyConstHandler());
    }

    private LetHandlerFactory() {

    }

    /**
     * @param part
     * @return the handler for the given strategy. null if not found.
     */
    public static LetHandler getHandlerFor(String part) {
        return strategies.get(part);
    }
}
