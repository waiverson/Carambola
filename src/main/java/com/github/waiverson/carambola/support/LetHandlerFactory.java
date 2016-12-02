package com.github.waiverson.carambola.support;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by waiverson on 16/11/25.
 */
public class LetHandlerFactory {

	private static Map<String, LetHandler> strategies = new HashMap<String, LetHandler>();

	static {
		strategies.put("header", new LetHeadHandler());
		strategies.put("body", new LetBodyHandler());
		strategies.put("body.xml", new LetBodyXmlHandler());
		strategies.put("js", new LetBodyJsHandler());
		strategies.put("const", new LetBodyConstHandler());
	}

	private LetHandlerFactory () {

	}

	public static LetHandler getHandlerFor(String part) {
		return strategies.get(part);
	}
}
