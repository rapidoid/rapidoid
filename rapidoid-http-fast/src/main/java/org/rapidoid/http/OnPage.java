package org.rapidoid.http;

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
import org.rapidoid.http.handler.*;
import org.rapidoid.io.Res;
import org.rapidoid.lambda.F2;
import org.rapidoid.lambda.F3;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;

import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class OnPage {

	private final ServerSetup chain;

	private final FastHttp[] httpImpls;

	private final String path;

	private volatile HttpWrapper[] wrappers;

	public OnPage(ServerSetup chain, FastHttp[] httpImpls, String path) {
		this.chain = chain;
		this.httpImpls = httpImpls;
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
				public Object handle(Req req) throws Exception {
					return response;
				}
			});
			return;
		}

		for (FastHttp http : httpImpls) {
			http.on("GET", path, new FastStaticHttpHandler(http, options.contentType, response));
			http.on("POST", path, new FastStaticHttpHandler(http, options.contentType, response));
		}
	}

	private void register(PageOptions options, ReqHandler handler) {
		for (FastHttp http : httpImpls) {
			FastParamsAwareHttpHandler hnd = new DelegatingFastParamsAwareReqHandler(http, options.contentType,
					wrappers, handler);
			http.on("GET", path, hnd);
			http.on("POST", path, hnd);
		}
	}

	private void register(PageOptions options, ReqRespHandler handler) {
		for (FastHttp http : httpImpls) {
			FastParamsAwareHttpHandler hnd = new DelegatingFastParamsAwareReqRespHandler(http, options.contentType,
					wrappers, handler);
			http.on("GET", path, hnd);
			http.on("POST", path, hnd);
		}
	}

	private void register(PageOptions options, final Res resource) {
		if (!U.isEmpty(wrappers)) {
			register(options, new ReqHandler() {
				@Override
				public Object handle(Req req) throws Exception {
					return resource.getBytes();
				}
			});
			return;
		}

		for (FastHttp http : httpImpls) {
			http.on("GET", path, new FastResourceHttpHandler(http, options.contentType, resource));
			http.on("POST", path, new FastResourceHttpHandler(http, options.contentType, resource));
		}
	}

	/* GUI */

	public ServerSetup gui(String response) {
		gui(response.getBytes());
		return chain;
	}

	public ServerSetup gui(byte[] response) {
		register(new PageOptions(MediaType.HTML_UTF_8, false), response);
		return chain;
	}

	public ServerSetup gui(final Object response) {
		return gui(new ReqHandler() {
			@Override
			public Object handle(Req req) throws Exception {
				return response;
			}
		});
	}

	public ServerSetup gui(final Callable<?> handler) {
		return gui(new ReqHandler() {
			@Override
			public Object handle(Req req) throws Exception {
				return handler.call();
			}
		});
	}

	public ServerSetup gui(ReqHandler handler) {
		register(new PageOptions(MediaType.HTML_UTF_8, false), handler);
		return chain;
	}

	public ServerSetup gui(ReqRespHandler handler) {
		register(new PageOptions(MediaType.HTML_UTF_8, false), handler);
		return chain;
	}

	public ServerSetup gui(Res resource) {
		register(new PageOptions(MediaType.HTML_UTF_8, false), resource);
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
