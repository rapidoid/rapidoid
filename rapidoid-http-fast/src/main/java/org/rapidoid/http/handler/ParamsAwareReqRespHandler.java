package org.rapidoid.http.handler;

/*
 * #%L
 * rapidoid-http-fast
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
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.net.abstracts.Channel;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class ParamsAwareReqRespHandler extends AbstractDecoratingHttpHandler {

	private final ReqRespHandler handler;

	public ParamsAwareReqRespHandler(FastHttp http, HttpRoutes routes, RouteOptions options, ReqRespHandler handler) {
		super(http, routes, options);
		this.handler = handler;
	}

	@Override
	protected Object handleReq(Channel ctx, boolean isKeepAlive, Req req, Object extra) throws Exception {
		return handler.execute(req, req.response());
	}

}
