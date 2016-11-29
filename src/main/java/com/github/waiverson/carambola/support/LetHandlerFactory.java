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
		strategies.put("header", new LetBodyHandler());
		strategies.put("header", new LetBodyXmlHandler());
		strategies.put("header", new LetBodyJsHandler());
		strategies.put("header", new LetBodyConstHandler());
	}

	private LetHandlerFactory () {

	}

	public static LetHandler getHandlerFor(String part) {
		return strategies.get(part);
	}
}
