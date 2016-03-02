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
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.RouteOptions;
import org.rapidoid.http.handler.HttpHandlers;
import org.rapidoid.lambda.*;
import org.rapidoid.util.Constants;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class OnPage {

	private final FastHttp http;

	private final String path;

	private final RouteOptions options = new RouteOptions();

	public OnPage(FastHttp http, String path) {
		this.http = http;
		this.path = path;
	}

	/* GUI */

	public void render(String response) {
		render(response.getBytes());
	}

	public void render(byte[] response) {
		HttpHandlers.register(http, Constants.GET_OR_POST, path, opts(), response);
	}

	public <T> void render(Callable<T> handler) {
		HttpHandlers.register(http, Constants.GET_OR_POST, path, opts(), handler);
	}

	public void render(Method method, Object instance) {
		HttpHandlers.register(http, Constants.GET_OR_POST, path, opts(), method, instance);
	}

	public void render(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(http, Constants.GET_OR_POST, path, opts(), handler);
	}

	public void render(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(http, Constants.GET_OR_POST, path, opts(), handler);
	}

	public void render(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(http, Constants.GET_OR_POST, path, opts(), handler);
	}

	public void render(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, Constants.GET_OR_POST, path, opts(), handler);
	}

	public void render(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, Constants.GET_OR_POST, path, opts(), handler);
	}

	public void render(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, Constants.GET_OR_POST, path, opts(), handler);
	}

	public void render(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, Constants.GET_OR_POST, path, opts(), handler);
	}

	/* CUSTOM ROUTE OPTIONS */

	private RouteOptions opts() {
		return options;
	}

	public OnPage wrap(HttpWrapper... wrappers) {
		options.wrap(wrappers);
		return this;
	}

	public OnPage roles(String... roles) {
		options.roles(roles);
		return this;
	}

	public OnPage title(String title) {
		options.title = title;
		return this;
	}

	public OnPage view(String viewName) {
		options.view = viewName;
		return this;
	}

}
