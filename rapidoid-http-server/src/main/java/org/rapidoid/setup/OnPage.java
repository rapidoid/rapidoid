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
import org.rapidoid.http.*;
import org.rapidoid.http.handler.FastHttpHandler;
import org.rapidoid.http.handler.FastStaticHttpHandler;
import org.rapidoid.http.handler.HttpHandlers;
import org.rapidoid.http.handler.optimized.FastCallableHttpHandler;
import org.rapidoid.lambda.*;
import org.rapidoid.u.U;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class OnPage {

	private static final String GET_POST = "GET,POST";

	private final Setup chain;

	private final FastHttp http;

	private final String path;

	private volatile HttpWrapper[] wrappers;

	public OnPage(Setup chain, FastHttp http, String path) {
		this.chain = chain;
		this.http = http;
		this.path = path;
	}

	public OnPage wrap(HttpWrapper... wrappers) {
		this.wrappers = wrappers;
		return this;
	}

	private void register(PageOptions options, final byte[] response) {
		if (!U.isEmpty(wrappers)) {
			register(options, new ReqHandler() {
				@Override
				public Object execute(Req req) throws Exception {
					return response;
				}
			});
			return;
		}

		http.on(GET_POST, path, new FastStaticHttpHandler(options.contentType, response));
	}

	private void register(PageOptions options, NParamLambda lambda) {
		FastHttpHandler handler = HttpHandlers.from(http, lambda, options.contentType, wrappers);
		http.on(GET_POST, path, handler);
	}

	private void register(PageOptions options, Callable<?> handler) {
		FastCallableHttpHandler hnd = new FastCallableHttpHandler(http, options.contentType, wrappers, (Callable<Object>) handler);
		http.on(GET_POST, path, hnd);
	}

	/* GUI */

	public Setup gui(String response) {
		gui(response.getBytes());
		return chain;
	}

	public Setup gui(byte[] response) {
		HttpHandlers.register(http, GET_POST, path, MediaType.HTML_UTF_8, wrappers, response);
		return chain;
	}

	public <T> Setup gui(Callable<T> handler) {
		HttpHandlers.register(http, GET_POST, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup gui(Method method, Object instance) {
		HttpHandlers.register(http, GET_POST, path, MediaType.HTML_UTF_8, wrappers, method, instance);
		return chain;
	}

	public Setup gui(OneParamLambda<?, ?> handler) {
		HttpHandlers.register(http, GET_POST, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup gui(TwoParamLambda<?, ?, ?> handler) {
		HttpHandlers.register(http, GET_POST, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup gui(ThreeParamLambda<?, ?, ?, ?> handler) {
		HttpHandlers.register(http, GET_POST, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup gui(FourParamLambda<?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, GET_POST, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup gui(FiveParamLambda<?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, GET_POST, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup gui(SixParamLambda<?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, GET_POST, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

	public Setup gui(SevenParamLambda<?, ?, ?, ?, ?, ?, ?, ?> handler) {
		HttpHandlers.register(http, GET_POST, path, MediaType.HTML_UTF_8, wrappers, handler);
		return chain;
	}

}
