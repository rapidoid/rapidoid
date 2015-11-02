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

import java.util.Map;
import java.util.concurrent.Callable;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.Res;
import org.rapidoid.lambda.F2;
import org.rapidoid.lambda.F3;
import org.rapidoid.lambda.Mapper;

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class OnPage {

	private final ServerSetup chain;

	private final FastHttp[] httpImpls;

	private final String path;

	public OnPage(ServerSetup chain, FastHttp[] httpImpls, String path) {
		this.chain = chain;
		this.httpImpls = httpImpls;
		this.path = path;
	}

	private void register(PageOptions options, byte[] response) {
		for (FastHttp http : httpImpls) {
			http.on("GET", path, new FastStaticHttpHandler(http, options.contentType, response));
			http.on("POST", path, new FastStaticHttpHandler(http, options.contentType, response));
		}
	}

	private void register(PageOptions options, ParamHandler handler) {
		for (FastHttp http : httpImpls) {
			http.on("GET", path, new FastParamsAwarePageHandler(http, options.contentType, handler));
			http.on("POST", path, new FastParamsAwarePageHandler(http, options.contentType, handler));
		}
	}

	private void register(PageOptions options, Res resource) {
		for (FastHttp http : httpImpls) {
			http.on("GET", path, new FastResourceHttpHandler(http, options.contentType, resource));
			http.on("POST", path, new FastResourceHttpHandler(http, options.contentType, resource));
		}
	}

	/*********************** GUI ***********************/

	public ServerSetup gui(String response) {
		gui(response.getBytes());
		return chain;
	}

	public ServerSetup gui(byte[] response) {
		register(new PageOptions(FastHttp.CONTENT_TYPE_HTML, false), response);
		return chain;
	}

	public ServerSetup gui(final Object response) {
		return gui(new ParamHandler() {
			@Override
			public Object handle(Map<String, Object> params) throws Exception {
				return response;
			}
		});
	}

	public ServerSetup gui(final Callable<?> handler) {
		return gui(new ParamHandler() {
			@Override
			public Object handle(Map<String, Object> params) throws Exception {
				return handler.call();
			}
		});
	}

	public ServerSetup gui(ParamHandler handler) {
		register(new PageOptions(FastHttp.CONTENT_TYPE_HTML, false), handler);
		return chain;
	}

	public ServerSetup gui(Res resource) {
		register(new PageOptions(FastHttp.CONTENT_TYPE_HTML, false), resource);
		return chain;
	}

	public ServerSetup gui(final String paramName, final Mapper<String, Object> handler) {
		return gui(HttpHandlers.parameterized(paramName, handler));
	}

	public ServerSetup gui(final String paramName1, final String paramName2, final F2<String, String, Object> handler) {
		return gui(HttpHandlers.parameterized(paramName1, paramName2, handler));
	}

	public ServerSetup gui(final String paramName1, final String paramName2, final String paramName3,
			final F3<String, String, String, Object> handler) {
		return gui(HttpHandlers.parameterized(paramName1, paramName2, paramName3, handler));
	}

}
