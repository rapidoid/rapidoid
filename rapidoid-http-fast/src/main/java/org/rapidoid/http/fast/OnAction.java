package org.rapidoid.http.fast;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.util.concurrent.Callable;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.Req;
import org.rapidoid.http.fast.handler.FastCallableHttpHandler;
import org.rapidoid.http.fast.handler.FastParamsAwareHttpHandler;
import org.rapidoid.http.fast.handler.FastResourceHttpHandler;
import org.rapidoid.http.fast.handler.FastStaticHttpHandler;
import org.rapidoid.http.fast.handler.HttpHandlers;
import org.rapidoid.io.Res;
import org.rapidoid.lambda.F2;
import org.rapidoid.lambda.F3;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.mime.MediaType;

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

	private void register(MediaType contentType, byte[] response) {
		for (FastHttp http : httpImpls) {
			http.on(verb, path, new FastStaticHttpHandler(http, contentType, response));
		}
	}

	private void register(MediaType contentType, final Object response) {
		register(contentType, new ReqHandler() {
			@Override
			public Object handle(Req req) throws Exception {
				return response;
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void register(MediaType contentType, Callable<?> handler) {
		for (FastHttp http : httpImpls) {
			http.on(verb, path, new FastCallableHttpHandler(http, contentType, wrappers, (Callable<Object>) handler));
		}
	}

	private void register(MediaType contentType, ReqHandler handler) {
		for (FastHttp http : httpImpls) {
			http.on(verb, path, new FastParamsAwareHttpHandler(http, contentType, wrappers, handler));
		}
	}

	private void register(MediaType contentType, Res resource) {
		for (FastHttp http : httpImpls) {
			http.on(verb, path, new FastResourceHttpHandler(http, contentType, resource));
		}
	}

	/*********************** PLAIN ***********************/

	public ServerSetup plain(String response) {
		plain(response.getBytes());
		return chain;
	}

	public ServerSetup plain(byte[] response) {
		register(MediaType.PLAIN_TEXT_UTF_8, response);
		return chain;
	}

	public ServerSetup plain(Object response) {
		register(MediaType.PLAIN_TEXT_UTF_8, response);
		return chain;
	}

	public <T> ServerSetup plain(Callable<T> handler) {
		register(MediaType.PLAIN_TEXT_UTF_8, handler);
		return chain;
	}

	public <T> ServerSetup plain(ReqHandler handler) {
		register(MediaType.PLAIN_TEXT_UTF_8, handler);
		return chain;
	}

	public <T> ServerSetup plain(Res resource) {
		register(MediaType.PLAIN_TEXT_UTF_8, resource);
		return chain;
	}

	/*********************** HTML ***********************/

	public ServerSetup html(String response) {
		html(response.getBytes());
		return chain;
	}

	public ServerSetup html(byte[] response) {
		register(MediaType.HTML_UTF_8, response);
		return chain;
	}

	public ServerSetup html(Object response) {
		register(MediaType.HTML_UTF_8, response);
		return chain;
	}

	public <T> ServerSetup html(Callable<T> handler) {
		register(MediaType.HTML_UTF_8, handler);
		return chain;
	}

	public <T> ServerSetup html(ReqHandler handler) {
		register(MediaType.HTML_UTF_8, handler);
		return chain;
	}

	public <T> ServerSetup html(Res resource) {
		register(MediaType.HTML_UTF_8, resource);
		return chain;
	}

	/*********************** JSON ***********************/

	public ServerSetup json(String response) {
		json(response.getBytes());
		return chain;
	}

	public ServerSetup json(byte[] response) {
		register(MediaType.JSON_UTF_8, response);
		return chain;
	}

	public ServerSetup json(Object response) {
		register(MediaType.JSON_UTF_8, response);
		return chain;
	}

	public <T> ServerSetup json(Callable<T> handler) {
		register(MediaType.JSON_UTF_8, handler);
		return chain;
	}

	public <T> ServerSetup json(ReqHandler handler) {
		register(MediaType.JSON_UTF_8, handler);
		return chain;
	}

	public <T> ServerSetup json(Res resource) {
		register(MediaType.JSON_UTF_8, resource);
		return chain;
	}

	/*********************** BINARY ***********************/

	public ServerSetup binary(String response) {
		binary(response.getBytes());
		return chain;
	}

	public ServerSetup binary(byte[] response) {
		register(MediaType.BINARY, response);
		return chain;
	}

	public ServerSetup binary(Object response) {
		register(MediaType.BINARY, response);
		return chain;
	}

	public <T> ServerSetup binary(Callable<T> handler) {
		register(MediaType.BINARY, handler);
		return chain;
	}

	public <T> ServerSetup binary(ReqHandler handler) {
		register(MediaType.BINARY, handler);
		return chain;
	}

	public <T> ServerSetup binary(Res resource) {
		register(MediaType.BINARY, resource);
		return chain;
	}

	/*********************** PARAMETERIZED ***********************/

	public ServerSetup plain(final String paramName, final Mapper<String, Object> handler) {
		return plain(HttpHandlers.parameterized(paramName, handler));
	}

	public ServerSetup plain(final String paramName1, final String paramName2, final F2<String, String, Object> handler) {
		return plain(HttpHandlers.parameterized(paramName1, paramName2, handler));
	}

	public ServerSetup plain(final String paramName1, final String paramName2, final String paramName3,
			final F3<String, String, String, Object> handler) {
		return plain(HttpHandlers.parameterized(paramName1, paramName2, paramName3, handler));
	}

	public ServerSetup html(final String paramName, final Mapper<String, Object> handler) {
		return html(HttpHandlers.parameterized(paramName, handler));
	}

	public ServerSetup html(final String paramName1, final String paramName2, final F2<String, String, Object> handler) {
		return html(HttpHandlers.parameterized(paramName1, paramName2, handler));
	}

	public ServerSetup html(final String paramName1, final String paramName2, final String paramName3,
			final F3<String, String, String, Object> handler) {
		return html(HttpHandlers.parameterized(paramName1, paramName2, paramName3, handler));
	}

	public ServerSetup json(final String paramName, final Mapper<String, Object> handler) {
		return json(HttpHandlers.parameterized(paramName, handler));
	}

	public ServerSetup json(final String paramName1, final String paramName2, final F2<String, String, Object> handler) {
		return json(HttpHandlers.parameterized(paramName1, paramName2, handler));
	}

	public ServerSetup json(final String paramName1, final String paramName2, final String paramName3,
			final F3<String, String, String, Object> handler) {
		return json(HttpHandlers.parameterized(paramName1, paramName2, paramName3, handler));
	}

	public ServerSetup binary(final String paramName, final Mapper<String, Object> handler) {
		return binary(HttpHandlers.parameterized(paramName, handler));
	}

	public ServerSetup binary(final String paramName1, final String paramName2, final F2<String, String, Object> handler) {
		return binary(HttpHandlers.parameterized(paramName1, paramName2, handler));
	}

	public ServerSetup binary(final String paramName1, final String paramName2, final String paramName3,
			final F3<String, String, String, Object> handler) {
		return binary(HttpHandlers.parameterized(paramName1, paramName2, paramName3, handler));
	}

}
