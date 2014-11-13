package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.inject.IoC;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.ExchangeProtocol;
import org.rapidoid.util.U;

public class HttpProtocol extends ExchangeProtocol<HttpExchangeImpl> {

	private final HttpParser parser = IoC.singleton(HttpParser.class);

	private final Router router;

	private final HttpResponses responses;

	private HttpSession session;

	public HttpProtocol(Router router) {
		super(HttpExchangeImpl.class);
		this.router = router;
		this.responses = new HttpResponses(true, true);
	}

	@Override
	protected void process(Channel ctx, HttpExchangeImpl x) {
		U.notNull(session, "session");

		if (ctx.isInitial()) {
			return;
		}

		parser.parse(x.input(), x.isGet, x.isKeepAlive, x.body, x.verb, x.uri, x.path, x.query, x.protocol, x.headers,
				x.helper());

		U.failIf(x.verb.isEmpty() || x.uri.isEmpty(), "Invalid HTTP request!");
		U.failIf(x.isGet.value && !x.body.isEmpty(), "Body is NOT allowed in HTTP GET requests!");

		x.setResponses(responses);
		x.setSession(session);

		try {
			boolean dispatched = router.dispatch(x);
			if (!dispatched) {
				x.notFound();
			}
		} catch (Throwable e) {
			U.error("Internal server error!", "request", x, "error", e);
			x.response(500, "Internal server error!", e);
		}

		if (!x.hasContentType()) {
			x.html();
		}

		x.completeResponse();
		x.closeIf(!x.isKeepAlive.value);
	}

	public Router getRouter() {
		return router;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

}
