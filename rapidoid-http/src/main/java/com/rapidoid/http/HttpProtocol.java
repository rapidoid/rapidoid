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

import org.rapidoid.Ctx;
import org.rapidoid.net.ExchangeProtocol;
import org.rapidoid.util.U;

public class HttpProtocol extends ExchangeProtocol<WebExchangeImpl> {

	private final HttpParser parser = U.inject(HttpParser.class);

	private final Router router;

	private final HttpResponses responses;

	public HttpProtocol(WebConfig config, Router router) {
		super(WebExchangeImpl.class);
		this.router = router;
		this.responses = new HttpResponses(!config.noserver(), !config.noserver());
	}

	@Override
	protected void process(Ctx ctx, WebExchangeImpl xch) {

		if (ctx.connection().isInitial()) {
			return;
		}

		parser.parse(xch.input(), xch.isGet, xch.isKeepAlive, xch.body, xch.verb, xch.uri, xch.path, xch.query,
				xch.protocol, xch.headers, xch.helper());

		U.failIf(xch.verb.isEmpty() || xch.uri.isEmpty(), "Invalid HTTP request!");
		U.failIf(xch.isGet.value && !xch.body.isEmpty(), "Body is NOT allowed in HTTP GET requests!");

		xch.respType = HttpResponses.TEXT_HTML;

		int posBefore = xch.output().size();

		xch.output().append(resp(xch).bytes());

		try {
			boolean dispatched = router.dispatch(xch);
			if (!dispatched) {
				xch.write("Invalid HTTP VERB or URL PATH!");
				xch.done();
			}
		} catch (Throwable e) {
			U.error("Internal server error!", "request", xch, "error", e);
			xch.write("Internal server error!");
			xch.done();
		}

		long wrote = xch.getTotalWritten();
		U.ensure(wrote <= Integer.MAX_VALUE, "Response too big!");

		int pos = posBefore + resp(xch).contentLengthPos;

		xch.output().putNumAsText(pos, wrote);
	}

	private HttpResponse resp(WebExchangeImpl xch) {
		HttpResponse resp = responses.get(xch.isKeepAlive.value, xch.respType);
		assert resp != null;
		return resp;
	}

}
