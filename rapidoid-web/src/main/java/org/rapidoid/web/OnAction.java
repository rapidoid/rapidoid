package org.rapidoid.web;

/*
 * #%L
 * rapidoid-http-fast
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
public class OnAction {

	private final ServerSetup chain;

	private final FastHttp[] httpImpls;

	private final String verb;

	private final String path;

	private volatile HttpWrapper[] wrappers;

	public OnAction(ServerSetup chain, FastHttp[] httpImpls, String verb, String path) {
		this.chain = chain;
		this.httpImpls = httpImpls;
		this.verb = verb;
		this.path = path;
	}

	public OnAction wrap(HttpWrapper... wrappers) {
		this.wrappers = wrappers;
		return this;
	}

	/* PLAIN */

	public ServerSetup plain(String response) {
		plain(response.getBytes());
		return chain;
	}

	public ServerSetup plain(byte[] response) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, response);
		return chain;
	}

	public <T> ServerSetup plain(Callable<T> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup plain(Method method, Object instance) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, method, instance);
		return chain;
	}

	public ServerSetup plain(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup plain(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup plain(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup plain(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup plain(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup plain(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup plain(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.PLAIN_TEXT_UTF_8, wrappers, handler);
		return chain;
	}

	/* HTML */

	public ServerSetup html(String response) {
		html(response.getBytes());
		return chain;
	}

	public ServerSetup html(byte[] response) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.HTML_UTF_8, wrappers, response);
		return chain;
	}

	public <T> ServerSetup html(Callable<T> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup html(Method method, Object instance) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.HTML_UTF_8, wrappers, method, instance);
		return chain;
	}

	public ServerSetup html(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup html(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup html(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup html(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup html(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup html(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup html(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	/* JSON */

	public ServerSetup json(String response) {
		json(response.getBytes());
		return chain;
	}

	public ServerSetup json(byte[] response) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.JSON_UTF_8, wrappers, response);
		return chain;
	}

	public <T> ServerSetup json(Callable<T> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup json(Method method, Object instance) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.JSON_UTF_8, wrappers, method, instance);
		return chain;
	}

	public ServerSetup json(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup json(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup json(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup json(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup json(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup json(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	public ServerSetup json(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.JSON_UTF_8, wrappers, handler);
		return chain;
	}

	/* BINARY */

	public ServerSetup binary(String response) {
		binary(response.getBytes());
		return chain;
	}

	public ServerSetup binary(byte[] response) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.BINARY, wrappers, response);
		return chain;
	}

	public <T> ServerSetup binary(Callable<T> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public ServerSetup binary(Method method, Object instance) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.BINARY, wrappers, method, instance);
		return chain;
	}

	public ServerSetup binary(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public ServerSetup binary(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public ServerSetup binary(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public ServerSetup binary(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public ServerSetup binary(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public ServerSetup binary(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

	public ServerSetup binary(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(httpImpls, verb, path, MediaType.BINARY, wrappers, handler);
		return chain;
	}

}
