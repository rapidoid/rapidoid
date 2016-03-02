package org.rapidoid.setup;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Coll;
import org.rapidoid.commons.MediaType;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.data.JSON;
import org.rapidoid.http.*;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.handler.FastHttpHandler;
import org.rapidoid.http.handler.optimized.DelegatingFastParamsAwareReqHandler;
import org.rapidoid.http.handler.optimized.DelegatingFastParamsAwareReqRespHandler;
import org.rapidoid.http.processor.HttpProcessor;
import org.rapidoid.ioc.IoC;
import org.rapidoid.ioc.IoCContext;
import org.rapidoid.job.Jobs;
import org.rapidoid.lambda.NParamLambda;
import org.rapidoid.log.Log;
import org.rapidoid.net.Server;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.Reload;
import org.rapidoid.util.UTILS;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

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
@Since("5.1.0")
public class Setup implements Constants {

	static final Setup ON = new Setup("on", "0.0.0.0", 8888, ServerSetupType.DEFAULT, IoC.defaultContext(), Conf.ON);
	static final Setup ADMIN = new Setup("admin", "0.0.0.0", 9999, ServerSetupType.ADMIN, IoC.createContext().name("admin"), Conf.ADMIN);
	static final Setup DEV = new Setup("dev", "127.0.0.1", 7777, ServerSetupType.DEV, IoC.createContext().name("dev"), Conf.DEV);

	private static final List<Setup> instances = Coll.synchronizedList(ON, ADMIN, DEV);

	static {
		RapidoidInitializer.initialize();

		Jobs.execute(new Runnable() {
			@Override
			public void run() {
				JSON.warmup();
			}
		});
	}

	private static volatile String mainClassName;
	private static volatile String appPkgName;
	private static volatile boolean dirty;

	static volatile boolean restarted;
	static volatile ClassLoader loader;

	static {
		resetGlobalState();
	}

	private final String name;
	private final Config config;

	private final String defaultAddress;
	private final int defaultPort;
	private final ServerSetupType setupType;

	private final IoCContext ioCContext;

	private final Customization customization = new Customization();
	private final HttpRoutes routes = new HttpRoutes(customization);
	private final FastHttp fastHttp = new FastHttp(routes, customization);

	private volatile Integer port;
	private volatile String address = "0.0.0.0";
	private volatile String[] path;

	private volatile HttpWrapper[] wrappers;
	private volatile HttpProcessor processor;

	private volatile boolean listening;
	private volatile Server server;
	private volatile boolean activated;

	public static Setup create(String name) {
		IoCContext ioc = IoC.createContext().name(name);
		Config config = Conf.section(name);
		Setup setup = new Setup(name, "0.0.0.0", 8888, ServerSetupType.CUSTOM, ioc, config);
		instances.add(setup);
		return setup;
	}

	public void destroy() {
		halt();
		instances.remove(this);
	}

	public Setup(String name, String defaultAddress, int defaultPort, ServerSetupType setupType, IoCContext ioCContext, Config config) {
		this.name = name;

		this.defaultAddress = defaultAddress;
		this.defaultPort = defaultPort;

		this.setupType = setupType;
		this.ioCContext = ioCContext;

		this.config = config;
	}

	public static void resetGlobalState() {
		mainClassName = null;
		appPkgName = null;
		restarted = false;
		loader = Setup.class.getClassLoader();
		dirty = false;
	}

	public FastHttp http() {
		return fastHttp;
	}

	public synchronized Server listen() {
		if (!listening && !restarted) {

			inferCallers();

			if (setupType != ServerSetupType.DEV || Conf.dev()) {
				listening = true;

				this.address = U.or(this.address, config.entry("address").or(defaultAddress));
				this.port = U.or(this.port, config.entry("port").or(defaultPort));

				HttpProcessor proc = processor != null ? processor : fastHttp;

				if (Conf.dev() && !OnChanges.isIgnored()) {
					proc = new AppRestartProcessor(this, proc);
					OnChanges.byDefaultRestart();
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
			listen();
		}
	}

	public OnRoute route(String verb, String path) {
		activate();
		return new OnRoute(this, http(), verb.toUpperCase(), path).wrap(wrappers);
	}

	public OnRoute get(String path) {
		activate();
		return new OnRoute(this, http(), GET, path).wrap(wrappers);
	}

	public OnRoute post(String path) {
		activate();
		return new OnRoute(this, http(), POST, path).wrap(wrappers);
	}

	public OnRoute put(String path) {
		activate();
		return new OnRoute(this, http(), PUT, path).wrap(wrappers);
	}

	public OnRoute delete(String path) {
		activate();
		return new OnRoute(this, http(), DELETE, path).wrap(wrappers);
	}

	public OnRoute patch(String path) {
		activate();
		return new OnRoute(this, http(), PATCH, path).wrap(wrappers);
	}

	public OnRoute options(String path) {
		activate();
		return new OnRoute(this, http(), OPTIONS, path).wrap(wrappers);
	}

	public OnRoute head(String path) {
		activate();
		return new OnRoute(this, http(), HEAD, path).wrap(wrappers);
	}

	public OnRoute trace(String path) {
		activate();
		return new OnRoute(this, http(), TRACE, path).wrap(wrappers);
	}

	public OnRoute getOrPost(String path) {
		activate();
		return new OnRoute(this, http(), GET_OR_POST, path).wrap(wrappers);
	}

	public OnPage page(String path) {
		activate();
		return new OnPage(this, http(), path).wrap(wrappers);
	}

	public Setup req(ReqHandler handler) {
		activate();
		routes.addGenericHandler(new DelegatingFastParamsAwareReqHandler(http(), MediaType.HTML_UTF_8, wrappers, handler));
		return this;
	}

	public Setup req(ReqRespHandler handler) {
		activate();
		routes.addGenericHandler(new DelegatingFastParamsAwareReqRespHandler(http(), MediaType.HTML_UTF_8, wrappers, handler));
		return this;
	}

	public Setup req(FastHttpHandler handler) {
		activate();
		routes.addGenericHandler(handler);
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
		port = null;
		address = null;
		path = null;
		processor = null;
		activated = false;
		ioCContext.reset();
	}

	public Server server() {
		return server;
	}

	public Map<String, Object> attributes() {
		return http().attributes();
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

	public Setup args(String... args) {
		config.args(args);
		return this;
	}

	public Setup bootstrap(String... args) {
		this.args(args);
		beans(annotated(Controller.class).in(path()).getAll().toArray());
		Log.info("Completed bootstrap", "context", getIoCContext());
		return this;
	}

	public OnAnnotated annotated(Class<? extends Annotation>... annotated) {
		return new OnAnnotated(annotated, path());
	}

	public Setup deregister(String verb, String path) {
		routes.remove(verb, path);
		return this;
	}

	public Setup deregister(Object... controllers) {
		PojoHandlersSetup.from(this, controllers).deregister();
		return this;
	}

	public IoCContext getIoCContext() {
		return ioCContext;
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

		Conf.reload();

		for (Setup setup : instances()) {
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

		String[] args = Conf.ROOT.getArgs();
		Cls.invoke(main, null, new Object[]{args});

		Log.info("Successfully restarted the application!");
	}

	public static List<Setup> instances() {
		return instances;
	}

	public Config config() {
		return config;
	}

	public Customization custom() {
		return customization;
	}

	public HttpRoutes getRoutes() {
		return routes;
	}

}
