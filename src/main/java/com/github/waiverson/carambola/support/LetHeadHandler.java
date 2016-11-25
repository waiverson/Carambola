package com.github.waiverson.carambola.support;

import com.github.waiverson.carambola.RunnerVariablesProvider;
import smartrics.rest.client.RestData.Header;
import smartrics.rest.client.RestResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by waiverson on 16/11/25.
 */

public class LetHeadHandler implements LetHandler {

	@Override
	public String handler(RunnerVariablesProvider variablesProvider,
						  RestResponse response, Object expressionContext, String expression) {
		List<String> content = new ArrayList<String>();
		if (response != null) {
			for (Header e : response.getHeaders()) {
				String string = Tools.convertEntryToString(e.getName(), e.getValue(), ":");
				content.add(string);
			}
		}



	}

}
