package com.rapidoid.http;

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

import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.ExchangeProtocol;
import org.rapidoid.util.U;

public class HttpProtocol extends ExchangeProtocol<HttpExchangeImpl> {

	private final HttpParser parser = U.singleton(HttpParser.class);

	private final Router router;

	private final HttpResponses responses;

	public HttpProtocol(Router router) {
		super(HttpExchangeImpl.class);
		this.router = router;
		this.responses = new HttpResponses(true, true);
	}

	@Override
	protected void process(Channel ctx, HttpExchangeImpl xch) {

		if (ctx.isInitial()) {
			return;
		}

		parser.parse(xch.input(), xch.isGet, xch.isKeepAlive, xch.body, xch.verb, xch.uri, xch.path, xch.query,
				xch.protocol, xch.headers, xch.helper());

		U.failIf(xch.verb.isEmpty() || xch.uri.isEmpty(), "Invalid HTTP request!");
		U.failIf(xch.isGet.value && !xch.body.isEmpty(), "Body is NOT allowed in HTTP GET requests!");

		int startingPos = xch.output().size();
		xch.output().append(resp(xch).bytes());

		try {
			boolean dispatched = router.dispatch(xch);
			if (!dispatched) {
				if (!xch.hasContentType()) {
					xch.html();
				}
				xch.write("Invalid HTTP VERB or URL PATH!");
				xch.done();
			}
		} catch (Throwable e) {
			U.error("Internal server error!", "request", xch, "error", e);
			if (!xch.hasContentType()) {
				xch.html();
			}
			xch.write("Internal server error!");
			xch.done();
		}

		long wrote = xch.output().size() - xch.bodyPos;
		U.must(wrote <= Integer.MAX_VALUE, "Response too big!");

		int pos = startingPos + resp(xch).contentLengthPos + 10;

		xch.output().putNumAsText(pos, wrote, false);
	}

	private HttpResponse resp(HttpExchangeImpl xch) {
		HttpResponse resp = responses.get(xch.isKeepAlive.value);
		assert resp != null;
		return resp;
	}

	public Router getRouter() {
		return router;
	}

}
