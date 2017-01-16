package org.rapidoid.http.handler.optimized;

/*
 * #%L
 * rapidoid-http-server
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.HttpRoutes;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.http.handler.AbstractDecoratingHttpHandler;
import org.rapidoid.lambda.OneParamLambda;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DelegatingParamsAwareRespHandler extends AbstractDecoratingHttpHandler {

	private final OneParamLambda<Object, Resp> handler;

	public DelegatingParamsAwareRespHandler(FastHttp http, HttpRoutes routes, RouteOptions options, OneParamLambda<?, ?> handler) {
		super(http, routes, options);
		this.handler = U.cast(handler);
	}

	@Override
	protected Object handleReq(Channel channel, boolean isKeepAlive, Req req, Object extra) throws Exception {
		return handler.execute(req.response());
	}

	@Override
	public String toString() {
		return contentTypeInfo("(Resp) -> ...");
	}

}
