package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.io.File;
import java.nio.ByteBuffer;

import org.rapidoid.inject.IoC;
import org.rapidoid.log.Log;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.ExchangeProtocol;
import org.rapidoid.util.AppCtx;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

public class HttpProtocol extends ExchangeProtocol<HttpExchangeImpl> {

	private final HttpParser parser = IoC.singleton(HttpParser.class);

	private final Router router;

	private final HttpResponses responses;

	private HttpSession session;

	private HTTPInterceptor interceptor;

	public HttpProtocol(Router router) {
		super(HttpExchangeImpl.class);
		this.router = router;
		this.responses = new HttpResponses(true, true);
	}

	@Override
	protected void process(Channel ctx, HttpExchangeImpl x) {
		U.notNull(responses, "responses");
		U.notNull(session, "session");
		U.notNull(router, "router");

		if (ctx.isInitial()) {
			return;
		}

		parser.parse(x.input(), x.isGet, x.isKeepAlive, x.body, x.verb, x.uri, x.path, x.query, x.protocol, x.headers,
				x.helper());

		U.rteIf(x.verb.isEmpty() || x.uri.isEmpty(), "Invalid HTTP request!");
		U.rteIf(x.isGet.value && !x.body.isEmpty(), "Body is NOT allowed in HTTP GET requests!");

		processRequest(x);
	}

	private void processRequest(HttpExchangeImpl x) {
		x.init(responses, session, router);

		AppCtx.setUser(x.user());

		try {

			try {
				if (interceptor != null) {
					interceptor.intercept(x);
				} else {
					x.run();
				}
			} catch (Throwable e) {
				handleError(x, e);
			}

			if (x.hasError()) {
				handleError(x, x.getError());
			} else if (!x.isAsync()) {
				x.completeResponse();
			}

		} finally {
			AppCtx.delUser();
		}
	}

	private void handleError(HttpExchangeImpl x, Throwable e) {
		Throwable cause = UTILS.rootCause(e);
		if (cause instanceof HttpExchangeException) {
			// redirect, notFound etc.
			x.completeResponse();
		} else {
			Log.error("Internal server error!", "request", x, "error", cause);
			x.errorResponse(e);
			x.completeResponse();
		}
	}

	public static void processResponse(HttpExchange xch, Object res) {

		HttpExchangeImpl x = (HttpExchangeImpl) xch;

		if (res != null) {
			if (res instanceof byte[]) {
				if (!x.hasContentType()) {
					x.binary();
				}
				x.write((byte[]) res);
			} else if (res instanceof String) {
				if (!x.hasContentType()) {
					x.json();
				}
				x.write((String) res);
			} else if (res instanceof ByteBuffer) {
				if (!x.hasContentType()) {
					x.binary();
				}
				x.write((ByteBuffer) res);
			} else if (res instanceof File) {
				File file = (File) res;
				x.sendFile(file);
			} else if (res.getClass().getSimpleName().endsWith("Page")) {
				x.html().write(res.toString());
			} else if (!(res instanceof HttpExchangeImpl)) {
				if (!x.hasContentType()) {
					x.json();
				}
				x.writeJSON(res);
			}

		} else {
			if (!x.hasContentType()) {
				x.html();
			}
			throw x.notFound();
		}
	}

	public Router getRouter() {
		return router;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public void setInterceptor(HTTPInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	public HTTPInterceptor getInterceptor() {
		return interceptor;
	}

}
