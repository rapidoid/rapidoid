package org.rapidoid.http.handler.optimized;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.Req;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.http.handler.AbstractAsyncHttpHandler;
import org.rapidoid.net.abstracts.Channel;

import java.util.concurrent.Callable;

/*
 * #%L
 * rapidoid-http-server
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

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class CallableHttpHandler extends AbstractAsyncHttpHandler {

	private final Callable<Object> handler;

	public CallableHttpHandler(FastHttp http, RouteOptions options, Callable<Object> handler) {
		super(http, options);
		this.handler = handler;
	}

	@Override
	protected Object handleReq(Channel ctx, boolean isKeepAlive, Req req, Object extra) throws Exception {
		return handler.call();
	}

	@Override
	public String toString() {
		return contentTypeInfo("() -> ...");
	}

}
