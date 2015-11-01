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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.0.0")
public class FastParamsAwarePageHandler extends AbstractResultHandlingFastHttpHandler implements HttpMetadata {

	private final ParamHandler handler;

	public FastParamsAwarePageHandler(FastHttp http, byte[] contentType, ParamHandler handler) {
		super(http, contentType);
		this.handler = handler;
	}

	@Override
	protected Object handleReq(Channel channel, Map<String, Object> params) throws Exception {
		Ctx ctx = Ctxs.open("page");

		// ctx.setApp(null);
		// ctx.setExchange(null);
		// ctx.setUser(user);

		ctx.setHost(U.get(params, HOST, ""));
		ctx.setVerb(U.get(params, VERB, ""));
		ctx.setPath(U.get(params, PATH, ""));
		ctx.setUri(U.get(params, URI, ""));

		U.assign(ctx.data(), params);
		// U.assign(ctx.session(), U.map());
		// U.assign(ctx.extras(), U.map());

		String resp;
		try {

			// call the handler, get the result
			Object result = handler.handle(params);

			// do data binding
			Beany.bind(result, params);

			// render the response and process logic while still in context
			resp = U.str(result);

		} finally {
			Ctxs.close();
		}

		return resp;
	}

	@Override
	public boolean needsParams() {
		return true;
	}

	@Override
	public boolean needsHeadersAndCookies() {
		return true;
	}

}
