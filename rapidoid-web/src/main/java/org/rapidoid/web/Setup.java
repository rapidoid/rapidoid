package org.rapidoid.web;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.MediaType;
import org.rapidoid.config.Conf;
import org.rapidoid.http.*;
import org.rapidoid.http.handler.FastHttpErrorHandler;
import org.rapidoid.http.handler.FastHttpHandler;
import org.rapidoid.http.handler.optimized.DelegatingFastParamsAwareReqHandler;
import org.rapidoid.http.handler.optimized.DelegatingFastParamsAwareReqRespHandler;
import org.rapidoid.http.processor.HttpProcessor;
import org.rapidoid.io.watch.Reload;
import org.rapidoid.ioc.IoC;
import org.rapidoid.ioc.IoCContext;
import org.rapidoid.lambda.NParamLambda;
import org.rapidoid.log.Log;
import org.rapidoid.net.Server;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.UTILS;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

/*
 * #%L
 * rapidoid-web
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
@Since("5.1.0")
public class Setup implements Constants {

	static final Setup DEFAULT = new Setup("http", "0.0.0.0", 8888, ServerSetupType.DEFAULT, IoC.defaultContext());
	static final Setup ADMIN = new Setup("admin", "0.0.0.0", 8889, ServerSetupType.ADMIN, IoC.defaultContext());
	static final Setup DEV = new Setup("dev", "127.0.0.1", 8887, ServerSetupType.DEV, IoC.defaultContext());

	private static volatile String mainClassName;
	private static volatile String appPkgName;

	static volatile boolean restarted = false;
	static volatile ClassLoader loader = Setup.class.getClassLoader();
	private static volatile boolean dirty = false;

	private final String name;
	private final String defaultAddress;
	private final int defaultPort;
	private final ServerSetupType setupType;

	private final IoCContext ioCContext;

	private final FastHttp fastHttp = new FastHttp();

	private volatile int port;

	private volatile String address = "0.0.0.0";

	private volatile String[] path;

	private volatile HttpWrapper[] wrappers;

	private volatile HttpProcessor processor;

	private volatile boolean listening;

	private volatile Server server;

	private volatile boolean activated;

	public Setup(String name, String defaultAddress, int defaultPort, ServerSetupType setupType, IoCContext ioCContext) {
		this.name = name;
		this.defaultAddress = defaultAddress;
		this.defaultPort = defaultPort;

		this.port = defaultPort;
		this.address = defaultAddress;
		this.setupType = setupType;
		this.ioCContext = ioCContext;
	}

	public FastHttp http() {
		return fastHttp;
	}

	public synchronized Server listen() {
		if (!listening && !restarted) {
			if (setupType != ServerSetupType.DEV || Conf.dev()) {
				listening = true;
				HttpProcessor proc = processor != null ? processor : fastHttp;
				if (Conf.dev()) {
					proc = new AppRestartProcessor(this, proc);
				}
				server = proc.listen(address, port);
			} else {
				Log.warn("The application is NOT running in dev mode, so the DEV server is automatically disabled.");
			}
		}

		return server;
	}

	private synchronized void activate() {
		if (!activated && !restarted) {
			activated = true;

			if (port == defaultPort) {
				int customPort = Conf.option(name + ".port", NOT_FOUND);
				if (customPort != NOT_FOUND) {
					port(customPort);
				}
			}

			listen();

			if (Conf.dev()) {
				On.changes().restart();
			}
		}
	}

	public OnAction get(String path) {
		activate();
		return new OnAction(this, httpImpls(), GET, path).wrap(wrappers);
	}

	public OnAction post(String path) {
		activate();
		return new OnAction(this, httpImpls(), POST, path).wrap(wrappers);
	}

	public OnAction put(String path) {
		activate();
		return new OnAction(this, httpImpls(), PUT, path).wrap(wrappers);
	}

	public OnAction delete(String path) {
		activate();
		return new OnAction(this, httpImpls(), DELETE, path).wrap(wrappers);
	}

	public OnAction patch(String path) {
		activate();
		return new OnAction(this, httpImpls(), PATCH, path).wrap(wrappers);
	}

	public OnAction options(String path) {
		activate();
		return new OnAction(this, httpImpls(), OPTIONS, path).wrap(wrappers);
	}

	public OnAction head(String path) {
		activate();
		return new OnAction(this, httpImpls(), HEAD, path).wrap(wrappers);
	}

	public OnAction trace(String path) {
		activate();
		return new OnAction(this, httpImpls(), TRACE, path).wrap(wrappers);
	}

	public OnPage page(String path) {
		activate();
		return new OnPage(this, httpImpls(), path).wrap(wrappers);
	}

	public Setup req(ReqHandler handler) {
		activate();
		for (FastHttp http : httpImpls()) {
			http.addGenericHandler(new DelegatingFastParamsAwareReqHandler(http, MediaType.HTML_UTF_8, wrappers,
					handler));
		}

		return this;
	}

	public Setup req(ReqRespHandler handler) {
		activate();
		for (FastHttp http : httpImpls()) {
			http.addGenericHandler(new DelegatingFastParamsAwareReqRespHandler(http, MediaType.HTML_UTF_8, wrappers,
					handler));
		}

		return this;
	}

	public Setup req(FastHttpHandler handler) {
		activate();

		for (FastHttp http : httpImpls()) {
			http.addGenericHandler(handler);
		}

		return this;
	}

	public Setup beans(Object... beans) {
		for (Object bean : beans) {
			if (bean instanceof NParamLambda) {
				throw U.rte("Expected a bean, but found lambda: " + bean);
			}
		}

		activate();

		PojoHandlersSetup.from(this, beans).register();

		return this;
	}

	public Setup onError(ErrorHandler onError) {
		for (FastHttp http : httpImpls()) {
			http.setErrorHandler(new FastHttpErrorHandler(http, onError));
		}

		return this;
	}

	private FastHttp[] httpImpls() {
		return new FastHttp[]{http()};
	}

	public Setup port(int port) {
		this.port = port;
		return this;
	}

	public Setup address(String address) {
		this.address = address;
		return this;
	}

	public Setup wrap(HttpWrapper... wrappers) {
		this.wrappers = wrappers;
		return this;
	}

	public Setup processor(HttpProcessor processor) {
		U.must(!listening, "The server was already initialized!");
		this.processor = processor;
		return this;
	}

	public Setup shutdown() {
		reset();
		if (this.server != null) {
			this.server.shutdown();
			this.server = null;
		}
		return this;
	}

	public Setup halt() {
		reset();
		if (this.server != null) {
			this.server.halt();
			this.server = null;
		}
		return this;
	}

	public void reset() {
		fastHttp.resetConfig();
		listening = false;
		wrappers = null;
		port = defaultPort;
		address = defaultAddress;
		path = null;
		processor = null;
		activated = false;
	}

	public Server server() {
		return server;
	}

	public Map<String, Object> attributes() {
		return http().attributes();
	}

	public Setup staticFilesPath(String... staticFilesLocations) {
		for (FastHttp http : httpImpls()) {
			http.setStaticFilesLocations(staticFilesLocations);
		}

		return this;
	}

	public Setup render(ViewRenderer renderer) {
		for (FastHttp http : httpImpls()) {
			http.setRenderer(renderer);
		}

		return this;
	}

	public Setup path(String... path) {
		this.path = path;
		return this;
	}

	public synchronized String[] path() {
		inferCallers();

		if (U.isEmpty(this.path)) {
			this.path = new String[]{appPkgName};
		}

		return path;
	}

	private static void inferCallers() {
		if (!restarted && appPkgName == null && mainClassName == null) {
			String pkg = UTILS.getCallingPackageOf(Setup.class, On.class, Setup.class);

			appPkgName = pkg;

			if (mainClassName == null) {
				Class<?> mainClass = UTILS.getCallingMainClass();
				mainClassName = mainClass != null ? mainClass.getName() : null;
			}

			Log.info("Inferring application root", "main class", mainClassName, "app package", pkg);
		}
	}

	public Setup bootstrap() {
		beans(annotated(Controller.class).in(path()).getAll().toArray());
		Log.info("Completed bootstrap", "context", getIoCContext());
		return this;
	}

	public OnAnnotated annotated(Class<? extends Annotation>... annotated) {
		return new OnAnnotated(annotated, path());
	}

	public Setup deregister(String verb, String path) {
		for (FastHttp http : httpImpls()) {
			http.remove(verb, path);
		}

		return this;
	}

	public Setup deregister(Object... controllers) {
		PojoHandlersSetup.from(this, controllers).deregister();
		return this;
	}

	public IoCContext getIoCContext() {
		return ioCContext;
	}

	static OnChanges onChanges() {
		inferCallers();
		return OnChanges.INSTANCE;
	}

	static void notifyChanges() {
		if (!dirty) {
			dirty = true;
			Log.info("Detected class or resource changes");
		}
	}

	static void restartIfDirty() {
		if (dirty) {
			synchronized (Setup.class) {
				if (dirty) {
					restartApp();
					dirty = false;
				}
			}
		}
	}

	private static void restartApp() {
		U.notNull(mainClassName, "Cannot restart, the main class is unknown!");

		Log.info("---------------------------------");
		Log.info("Restarting the web application...");
		Log.info("---------------------------------");

		restarted = true;

		for (Setup setup : setups()) {
			setup.fastHttp.resetConfig();
			setup.wrappers = null;
		}

		loader = Reload.createClassLoader();
		ClasspathUtil.setDefaultClassLoader(loader);

		Class<?> entry;
		try {
			entry = loader.loadClass(mainClassName);
		} catch (ClassNotFoundException e) {
			Log.error("Cannot restart the application, the main class (app entry point) is missing!");
			return;
		}

		Method main = Cls.getMethod(entry, "main", String[].class);
		U.must(main.getReturnType() == void.class);

		String[] args = Conf.getArgs();
		Cls.invoke(main, null, new Object[]{args});

		Log.info("Successfully restarted the application!");
	}

	private static Setup[] setups() {
		return new Setup[]{DEFAULT, ADMIN, DEV};
	}

}

