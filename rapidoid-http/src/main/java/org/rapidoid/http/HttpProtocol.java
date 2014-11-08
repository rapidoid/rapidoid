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
	protected void process(Channel ctx, HttpExchangeImpl xch) {
		U.notNull(session, "session");

		if (ctx.isInitial()) {
			return;
		}

		parser.parse(xch.input(), xch.isGet, xch.isKeepAlive, xch.body, xch.verb, xch.uri, xch.path, xch.query,
				xch.protocol, xch.headers, xch.helper());

		U.failIf(xch.verb.isEmpty() || xch.uri.isEmpty(), "Invalid HTTP request!");
		U.failIf(xch.isGet.value && !xch.body.isEmpty(), "Body is NOT allowed in HTTP GET requests!");

		xch.setResponses(responses);
		xch.setSession(session);

		try {
			boolean dispatched = router.dispatch(xch);
			if (!dispatched) {
				xch.notFound();
			}
		} catch (Throwable e) {
			U.error("Internal server error!", "request", xch, "error", e);
			xch.response(500, "Internal server error!", e);
		}

		xch.completeResponse();

		xch.closeIf(!xch.isKeepAlive.value);
	}

	public Router getRouter() {
		return router;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

}
