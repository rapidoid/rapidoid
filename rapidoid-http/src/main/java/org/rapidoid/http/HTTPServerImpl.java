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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.session.InMemorySessionStore;
import org.rapidoid.http.session.SessionStore;
import org.rapidoid.json.JSON;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.impl.RapidoidServerLoop;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HTTPServerImpl extends RapidoidServerLoop implements HTTPServer {

	private final SessionStore session = new InMemorySessionStore();

	public HTTPServerImpl() {
		super(new HttpProtocol(new HttpRouter()), HttpExchangeImpl.class, null);
		((HttpProtocol) protocol).setSessionStore(session);
	}

	@Override
	public HTTPServer route(String cmd, String url, Handler handler) {
		router().route(cmd, url, handler);
		return this;
	}

	@Override
	public HTTPServer route(String cmd, String url, String response) {
		router().route(cmd, url, contentHandler(response));
		return this;
	}

	@Override
	public HTTPServer serve(Handler handler) {
		router().generic(handler);
		return this;
	}

	@Override
	public HTTPServer serve(String response) {
		return serve(contentHandler(response));
	}

	private Router router() {
		return ((HttpProtocol) protocol).getRouter();
	}

	@Override
	public HTTPServer get(String url, Handler handler) {
		return route("GET", url, handler);
	}

	@Override
	public HTTPServer post(String url, Handler handler) {
		return route("POST", url, handler);
	}

	@Override
	public HTTPServer put(String url, Handler handler) {
		return route("PUT", url, handler);
	}

	@Override
	public HTTPServer delete(String url, Handler handler) {
		return route("DELETE", url, handler);
	}

	private static Handler contentHandler(String response) {
		final byte[] bytes = response.getBytes();

		return new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				x.html();
				return bytes;
			}
		};
	}

	@Override
	public HTTPServer start() {
		super.start();
		JSON.warmup();
		return this;
	}

	@Override
	public HTTPServer shutdown() {
		super.shutdown();
		return this;
	}

	@Override
	public HTTPInterceptor interceptor() {
		return ((HttpProtocol) protocol).getInterceptor();
	}

	@Override
	public HTTPServer interceptor(HTTPInterceptor interceptor) {
		((HttpProtocol) protocol).setInterceptor(interceptor);
		return this;
	}

	@Override
	public HTTPServer addUpgrade(String upgradeName, HttpUpgradeHandler upgradeHandler, Protocol upgradeProtocol) {
		((HttpProtocol) protocol).addUpgrade(upgradeName, upgradeHandler, upgradeProtocol);
		return this;
	}

}
