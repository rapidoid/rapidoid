package org.rapidoid.http.fast;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.MediaType;
import org.rapidoid.config.Conf;
import org.rapidoid.http.fast.handler.*;
import org.rapidoid.http.fast.listener.FastHttpListener;
import org.rapidoid.http.fast.listener.IgnorantHttpListener;
import org.rapidoid.net.Serve;
import org.rapidoid.net.TCPServer;
import org.rapidoid.pojo.POJO;
import org.rapidoid.pojo.PojoDispatcher;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

/*
 * #%L
 * rapidoid-http-fast
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
public class ServerSetup {

	private volatile int port = Conf.port();

	private volatile String address = "0.0.0.0";

	private volatile FastHttpListener listener = new IgnorantHttpListener();

	private volatile HttpWrapper[] wrappers;

	private volatile FastHttp fastHttp;

	private volatile boolean listening;

	private volatile TCPServer server;

	public synchronized FastHttp http() {
		if (fastHttp == null) {
			fastHttp = new FastHttp(listener);
		}

		return fastHttp;
	}

	public synchronized TCPServer listen() {
		if (!listening) {
			listening = true;
			server = Serve.server().protocol(http()).address(address).port(port).build();
			server.start();
		}

		return server;
	}

	public OnAction get(String path) {
		return new OnAction(this, httpImpls(), "GET", path).wrap(wrappers);
	}

	public OnAction post(String path) {
		return new OnAction(this, httpImpls(), "POST", path).wrap(wrappers);
	}

	public OnAction put(String path) {
		return new OnAction(this, httpImpls(), "PUT", path).wrap(wrappers);
	}

	public OnAction delete(String path) {
		return new OnAction(this, httpImpls(), "DELETE", path).wrap(wrappers);
	}

	public OnAction patch(String path) {
		return new OnAction(this, httpImpls(), "PATCH", path).wrap(wrappers);
	}

	public OnAction options(String path) {
		return new OnAction(this, httpImpls(), "OPTIONS", path).wrap(wrappers);
	}

	public OnAction head(String path) {
		return new OnAction(this, httpImpls(), "HEAD", path).wrap(wrappers);
	}

	public OnAction trace(String path) {
		return new OnAction(this, httpImpls(), "TRACE", path).wrap(wrappers);
	}

	public OnPage page(String path) {
		return new OnPage(this, httpImpls(), path).wrap(wrappers);
	}

	public ServerSetup req(ReqHandler handler) {
		for (FastHttp http : httpImpls()) {
			http.addGenericHandler(new DelegatingFastParamsAwareReqHandler(http, MediaType.HTML_UTF_8, wrappers,
					handler));
		}

		return this;
	}

	public ServerSetup req(ReqRespHandler handler) {
		for (FastHttp http : httpImpls()) {
			http.addGenericHandler(new DelegatingFastParamsAwareReqRespHandler(http, MediaType.HTML_UTF_8, wrappers,
					handler));
		}

		return this;
	}

	public ServerSetup req(FastHttpHandler handler) {
		for (FastHttp http : httpImpls()) {
			http.addGenericHandler(handler);
		}

		return this;
	}

	public ServerSetup req(Object... controllers) {
		List<Object> pojos = U.list();

		for (Object controller : controllers) {

			if (controller instanceof ReqHandler) {
				ReqHandler handler = (ReqHandler) controller;
				req(handler);

			} else if (controller instanceof ReqRespHandler) {
				ReqRespHandler handler = (ReqRespHandler) controller;
				req(handler);

			} else if (controller instanceof FastHttpHandler) {
				FastHttpHandler handler = (FastHttpHandler) controller;
				req(handler);

			} else {
				pojos.add(controller);
			}
		}

		PojoDispatcher dispatcher = POJO.dispatcher(pojos.toArray());

		for (FastHttp http : httpImpls()) {
			http.addGenericHandler(new PojoHandler(http, dispatcher));
		}

		return this;
	}

	public ServerSetup onError(ErrorHandler onError) {
		for (FastHttp http : httpImpls()) {
			http.setErrorHandler(new FastHttpErrorHandler(http, onError));
		}

		return this;
	}

	private FastHttp[] httpImpls() {
		return new FastHttp[]{http()};
	}

	public ServerSetup port(int port) {
		this.port = port;
		return this;
	}

	public ServerSetup address(String address) {
		this.address = address;
		return this;
	}

	public ServerSetup defaultWrap(HttpWrapper... wrappers) {
		this.wrappers = wrappers;
		return this;
	}

	public ServerSetup listener(FastHttpListener listener) {
		U.must(this.fastHttp == null, "The HTTP server was already initialized!");
		this.listener = listener;
		return this;
	}

	public ServerSetup shutdown() {
		reset();
		this.server.shutdown();
		this.server = null;
		return this;
	}

	public ServerSetup halt() {
		reset();
		this.server.halt();
		this.server = null;
		return this;
	}

	private void reset() {
		fastHttp.resetConfig();
		listening = false;
		fastHttp = null;
		wrappers = null;
	}

	public TCPServer server() {
		return server;
	}

	public Map<String, Object> attributes() {
		return http().attributes();
	}

	public ServerSetup staticFilesPath(String... staticFilesLocations) {
		for (FastHttp http : httpImpls()) {
			http.setStaticFilesLocations(staticFilesLocations);
		}

		return this;
	}

	public ServerSetup render(ViewRenderer renderer) {
		for (FastHttp http : httpImpls()) {
			http.setRenderer(renderer);
		}

		return this;
	}

}
