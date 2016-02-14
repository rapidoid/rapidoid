package org.rapidoid.http;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.MediaType;
import org.rapidoid.config.Conf;
import org.rapidoid.http.handler.FastHttpErrorHandler;
import org.rapidoid.http.handler.FastHttpHandler;
import org.rapidoid.http.handler.optimized.DelegatingFastParamsAwareReqHandler;
import org.rapidoid.http.handler.optimized.DelegatingFastParamsAwareReqRespHandler;
import org.rapidoid.http.listener.FastHttpListener;
import org.rapidoid.http.listener.IgnorantHttpListener;
import org.rapidoid.log.Log;
import org.rapidoid.net.Serve;
import org.rapidoid.net.TCPServer;
import org.rapidoid.pojo.PojoHandlersSetup;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

import java.lang.annotation.Annotation;
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

	public static final int UNDEFINED = -1;

	private final String name;
	private final String defaultAddress;
	private final int defaultPort;
	private final ServerSetupType setupType;

	private volatile FastHttpListener listener = new IgnorantHttpListener();

	private volatile int port;
	private volatile String address = "0.0.0.0";

	private volatile String[] path;

	private volatile HttpWrapper[] wrappers;

	private volatile FastHttp fastHttp;

	private volatile boolean listening;

	private volatile TCPServer server;

	public ServerSetup(String name, String defaultAddress, int defaultPort, ServerSetupType setupType) {
		this.name = name;
		this.defaultAddress = defaultAddress;
		this.defaultPort = defaultPort;

		this.port = defaultPort;
		this.address = defaultAddress;
		this.setupType = setupType;
	}

	public synchronized FastHttp http() {
		if (fastHttp == null) {
			fastHttp = new FastHttp(listener);
		}

		return fastHttp;
	}

	public synchronized TCPServer listen() {
		if (!listening) {
			if (setupType != ServerSetupType.DEV || Conf.dev()) {
				listening = true;
				server = Serve.server().protocol(http()).address(address).port(port).build();
				server.start();
			} else {
				Log.warn("The application is NOT running in dev mode, so the DEV server is automatically disabled.");
			}
		}

		return server;
	}

	private void activate() {
		if (port == defaultPort) {
			int customPort = Conf.option(name + ".port", UNDEFINED);
			if (customPort != UNDEFINED) {
				port(customPort);
			}
		}

		listen();
	}

	public OnAction get(String path) {
		activate();
		return new OnAction(this, httpImpls(), "GET", path).wrap(wrappers);
	}

	public OnAction post(String path) {
		activate();
		return new OnAction(this, httpImpls(), "POST", path).wrap(wrappers);
	}

	public OnAction put(String path) {
		activate();
		return new OnAction(this, httpImpls(), "PUT", path).wrap(wrappers);
	}

	public OnAction delete(String path) {
		activate();
		return new OnAction(this, httpImpls(), "DELETE", path).wrap(wrappers);
	}

	public OnAction patch(String path) {
		activate();
		return new OnAction(this, httpImpls(), "PATCH", path).wrap(wrappers);
	}

	public OnAction options(String path) {
		activate();
		return new OnAction(this, httpImpls(), "OPTIONS", path).wrap(wrappers);
	}

	public OnAction head(String path) {
		activate();
		return new OnAction(this, httpImpls(), "HEAD", path).wrap(wrappers);
	}

	public OnAction trace(String path) {
		activate();
		return new OnAction(this, httpImpls(), "TRACE", path).wrap(wrappers);
	}

	public OnPage page(String path) {
		activate();
		return new OnPage(this, httpImpls(), path).wrap(wrappers);
	}

	public ServerSetup req(ReqHandler handler) {
		activate();
		for (FastHttp http : httpImpls()) {
			http.addGenericHandler(new DelegatingFastParamsAwareReqHandler(http, MediaType.HTML_UTF_8, wrappers,
					handler));
		}

		return this;
	}

	public ServerSetup req(ReqRespHandler handler) {
		activate();
		for (FastHttp http : httpImpls()) {
			http.addGenericHandler(new DelegatingFastParamsAwareReqRespHandler(http, MediaType.HTML_UTF_8, wrappers,
					handler));
		}

		return this;
	}

	public ServerSetup req(FastHttpHandler handler) {
		activate();
		for (FastHttp http : httpImpls()) {
			http.addGenericHandler(handler);
		}

		return this;
	}

	public ServerSetup req(Object... controllers) {
		activate();
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

		PojoHandlersSetup.from(this, pojos.toArray()).register();

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
		if (this.server != null) {
			this.server.shutdown();
			this.server = null;
		}
		return this;
	}

	public ServerSetup halt() {
		reset();
		if (this.server != null) {
			this.server.halt();
			this.server = null;
		}
		return this;
	}

	public void reset() {
		if (fastHttp != null) {
			fastHttp.resetConfig();
		}

		listening = false;
		fastHttp = null;
		wrappers = null;
		listener = new IgnorantHttpListener();
		port = defaultPort;
		address = defaultAddress;
		path = null;
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

	public ServerSetup path(String... path) {
		this.path = path;
		return this;
	}

	public synchronized String[] path() {
		if (U.isEmpty(this.path)) {
			String pkg = UTILS.getCallingPackageOf(ServerSetup.class, On.class, ServerSetup.class);
			this.path = new String[]{pkg};
			Log.info("Inferring application package (path) to be: " + pkg);
		}

		return path;
	}

	public ServerSetup bootstrap() {
		req(annotated(Controller.class).in(path()).getAll().toArray());
		return this;
	}

	public OnAnnotated annotated(Class<? extends Annotation>... annotated) {
		return new OnAnnotated(annotated, path());
	}

	public ServerSetup deregister(String verb, String path) {
		for (FastHttp http : httpImpls()) {
			http.remove(verb, path);
		}

		return this;
	}

	public ServerSetup deregister(Object... controllers) {
		PojoHandlersSetup.from(this, controllers).deregister();
		return this;
	}

	public OnChanges changes() {
		return new OnChanges(this, httpImpls());
	}

}
