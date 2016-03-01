package org.rapidoid.setup;

/*
 * #%L
 * rapidoid-http-server
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.MediaType;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.handler.HttpHandlers;
import org.rapidoid.lambda.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class OnRoute {

	private final Setup chain;

	private final FastHttp http;

	private final String verb;

	private final String path;

	private volatile HttpWrapper[] wrappers;

	public OnRoute(Setup chain, FastHttp http, String verb, String path) {
		this.chain = chain;
		this.http = http;
		this.verb = verb;
		this.path = path;
	}

	public OnRoute wrap(HttpWrapper... wrappers) {
		this.wrappers = wrappers;
		return this;
	}

	/* PLAIN */

	public Setup plain(String response) {
		plain(response.getBytes());
		return chain;
	}

	public Setup plain(byte[] response) {
		HttpHandlers.register(http, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, response);
		return chain;
	}

	public <T> Setup plain(Callable<T> handler) {
		HttpHandlers.register(http, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup plain(Method method, Object instance) {
		HttpHandlers.register(http, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, method, instance);
		return chain;
	}

	public Setup plain(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup plain(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup plain(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup plain(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup plain(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup plain(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup plain(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	/* HTML */

	public Setup html(String response) {
		html(response.getBytes());
		return chain;
	}

	public Setup html(byte[] response) {
		HttpHandlers.register(http, verb, path, MediaType.HTML_UTF_8, wrappers, response);
		return chain;
	}

	public <T> Setup html(Callable<T> handler) {
		HttpHandlers.register(http, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup html(Method method, Object instance) {
		HttpHandlers.register(http, verb, path, MediaType.HTML_UTF_8, wrappers, method, instance);
		return chain;
	}

	public Setup html(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup html(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup html(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup html(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup html(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup html(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup html(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	/* JSON */

	public Setup json(String response) {
		json(response.getBytes());
		return chain;
	}

	public Setup json(byte[] response) {
		HttpHandlers.register(http, verb, path, MediaType.JSON_UTF_8, wrappers, response);
		return chain;
	}

	public <T> Setup json(Callable<T> handler) {
		HttpHandlers.register(http, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup json(Method method, Object instance) {
		HttpHandlers.register(http, verb, path, MediaType.JSON_UTF_8, wrappers, method, instance);
		return chain;
	}

	public Setup json(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup json(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup json(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup json(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup json(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup json(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup json(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	/* BINARY */

	public Setup binary(String response) {
		binary(response.getBytes());
		return chain;
	}

	public Setup binary(byte[] response) {
		HttpHandlers.register(http, verb, path, MediaType.BINARY, wrappers, response);
		return chain;
	}

	public <T> Setup binary(Callable<T> handler) {
		HttpHandlers.register(http, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public Setup binary(Method method, Object instance) {
		HttpHandlers.register(http, verb, path, MediaType.BINARY, wrappers, method, instance);
		return chain;
	}

	public Setup binary(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public Setup binary(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public Setup binary(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public Setup binary(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public Setup binary(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public Setup binary(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public Setup binary(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

}
