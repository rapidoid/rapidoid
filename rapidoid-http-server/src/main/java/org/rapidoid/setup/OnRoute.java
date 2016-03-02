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
import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.commons.MediaType;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.RouteOptions;
import org.rapidoid.http.handler.HttpHandlers;
import org.rapidoid.lambda.*;
import org.rapidoid.u.U;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class OnRoute {

	private final FastHttp http;

	private final String verb;

	private final String path;

	private final RouteOptions options = new RouteOptions();

	public OnRoute(FastHttp http, String verb, String path) {
		this.http = http;
		this.verb = verb;
		this.path = path;
	}

	/* PLAIN */

	public void plain(String response) {
		plain(response.getBytes());
	}

	public void plain(byte[] response) {
		HttpHandlers.register(http, verb, path, plainOpts(), response);
	}

	public <T> void plain(Callable<T> handler) {
		HttpHandlers.register(http, verb, path, plainOpts(), handler);
	}

	public void plain(Method method, Object instance) {
		HttpHandlers.register(http, verb, path, plainOpts(), method, instance);
	}

	public void plain(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(http, verb, path, plainOpts(), handler);
	}

	public void plain(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, plainOpts(), handler);
	}

	public void plain(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, plainOpts(), handler);
	}

	public void plain(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, plainOpts(), handler);
	}

	public void plain(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, plainOpts(), handler);
	}

	public void plain(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, plainOpts(), handler);
	}

	public void plain(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, plainOpts(), handler);
	}

	/* HTML */

	public void html(String response) {
		html(response.getBytes());
	}

	public void html(byte[] response) {
		HttpHandlers.register(http, verb, path, htmlOpts(), response);
	}

	public <T> void html(Callable<T> handler) {
		HttpHandlers.register(http, verb, path, htmlOpts(), handler);
	}

	public void html(Method method, Object instance) {
		HttpHandlers.register(http, verb, path, htmlOpts(), method, instance);
	}

	public void html(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(http, verb, path, htmlOpts(), handler);
	}

	public void html(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, htmlOpts(), handler);
	}

	public void html(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, htmlOpts(), handler);
	}

	public void html(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, htmlOpts(), handler);
	}

	public void html(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, htmlOpts(), handler);
	}

	public void html(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, htmlOpts(), handler);
	}

	public void html(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, htmlOpts(), handler);
	}

	/* JSON */

	public void json(String response) {
		json(response.getBytes());
	}

	public void json(byte[] response) {
		HttpHandlers.register(http, verb, path, jsonOpts(), response);
	}

	public <T> void json(Callable<T> handler) {
		HttpHandlers.register(http, verb, path, jsonOpts(), handler);
	}

	public void json(Method method, Object instance) {
		HttpHandlers.register(http, verb, path, jsonOpts(), method, instance);
	}

	public void json(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(http, verb, path, jsonOpts(), handler);
	}

	public void json(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, jsonOpts(), handler);
	}

	public void json(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, jsonOpts(), handler);
	}

	public void json(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, jsonOpts(), handler);
	}

	public void json(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, jsonOpts(), handler);
	}

	public void json(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, jsonOpts(), handler);
	}

	public void json(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, jsonOpts(), handler);
	}

	/* BINARY */

	public void binary(String response) {
		binary(response.getBytes());
	}

	public void binary(byte[] response) {
		HttpHandlers.register(http, verb, path, binaryOpts(), response);
	}

	public <T> void binary(Callable<T> handler) {
		HttpHandlers.register(http, verb, path, binaryOpts(), handler);
	}

	public void binary(Method method, Object instance) {
		HttpHandlers.register(http, verb, path, binaryOpts(), method, instance);
	}

	public void binary(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(http, verb, path, binaryOpts(), handler);
	}

	public void binary(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, binaryOpts(), handler);
	}

	public void binary(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, binaryOpts(), handler);
	}

	public void binary(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, binaryOpts(), handler);
	}

	public void binary(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, binaryOpts(), handler);
	}

	public void binary(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, binaryOpts(), handler);
	}

	public void binary(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, binaryOpts(), handler);
	}

	/* GUI */

	public void render(String response) {
		HttpHandlers.registerPredefined(http, verb, path, renderOpts(), response);
	}

	public void render(Collection<?> response) {
		HttpHandlers.registerPredefined(http, verb, path, renderOpts(), response);
	}

	public void render(Map<?, ?> response) {
		HttpHandlers.registerPredefined(http, verb, path, renderOpts(), response);
	}

	public <T> void render(Callable<T> handler) {
		HttpHandlers.register(http, verb, path, renderOpts(), handler);
	}

	public void render(Method method, Object instance) {
		HttpHandlers.register(http, verb, path, renderOpts(), method, instance);
	}

	public void render(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(http, verb, path, renderOpts(), handler);
	}

	public void render(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, renderOpts(), handler);
	}

	public void render(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, renderOpts(), handler);
	}

	public void render(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, renderOpts(), handler);
	}

	public void render(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, renderOpts(), handler);
	}

	public void render(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, renderOpts(), handler);
	}

	public void render(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, verb, path, renderOpts(), handler);
	}
	
	/* CONTENT TYPE */

	private RouteOptions plainOpts() {
		return opts(MediaType.PLAIN_TEXT_UTF_8);
	}

	private RouteOptions htmlOpts() {
		return opts(MediaType.HTML_UTF_8);
	}

	private RouteOptions jsonOpts() {
		return opts(MediaType.JSON_UTF_8);
	}

	private RouteOptions binaryOpts() {
		return opts(MediaType.BINARY);
	}

	private RouteOptions renderOpts() {
		return opts(MediaType.HTML_UTF_8).render();
	}

	private RouteOptions opts(MediaType contentType) {
		options.contentType = contentType;
		return options;
	}

	/* ROUTE OPTIONS */

	public OnRoute wrap(HttpWrapper... wrappers) {
		options.wrap(wrappers);
		return this;
	}

	public OnRoute roles(String... roles) {
		options.roles(U.set(roles));
		return this;
	}

	public OnRoute title(String title) {
		options.title = title;
		return this;
	}

	public OnRoute view(String viewName) {
		options.view = viewName;
		return this;
	}

	public OnRoute tx(TransactionMode txMode) {
		options.tx = txMode;
		return this;
	}
}
