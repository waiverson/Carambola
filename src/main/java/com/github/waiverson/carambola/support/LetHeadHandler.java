package com.github.waiverson.carambola.support;

import com.github.waiverson.carambola.RunnerVariablesProvider;
import smartrics.rest.client.RestData.Header;
import smartrics.rest.client.RestResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by waiverson on 16/11/25.
 */

public class LetHeadHandler implements LetHandler {

	public String handle(RunnerVariablesProvider variablesProvider,
						  RestResponse response, Object expressionContext, String expression) {
		List<String> content = new ArrayList<String>();
		if (response != null) {
			for (Header e : response.getHeaders()) {
				String string = Tools.convertEntryToString(e.getName(), e.getValue(), ":");
				content.add(string);
			}
		}

		String value = null;
		if (content.size() > 0) {
			Pattern p = Pattern.compile(expression);
			for (String c : content) {
				Matcher m = p.matcher(c);
				if ( m.find()) {
					int cc = m.groupCount();
					value = m.group(cc);
					break;
				}
			}
		}

		return value;
	}

}
