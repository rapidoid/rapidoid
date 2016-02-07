package org.rapidoid.http.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.handler.FastHttpHandler;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public interface HandlerMatch {

	FastHttpHandler getHandler();

	Map<String, String> getParams();

}
