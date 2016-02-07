package org.rapidoid.http.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.handler.FastHttpHandler;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HandlerMatchWithParams implements HandlerMatch {

	public final FastHttpHandler handler;

	public final Map<String, String> params;

	public HandlerMatchWithParams(FastHttpHandler handler, Map<String, String> params) {
		this.handler = handler;
		this.params = params;
	}

	@Override
	public FastHttpHandler getHandler() {
		return handler;
	}

	@Override
	public Map<String, String> getParams() {
		return params;
	}

}
