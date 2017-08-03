package org.rapidoid.setup;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.AnyObj;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.data.JSON;
import org.rapidoid.env.Env;
import org.rapidoid.env.RapidoidEnv;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.HttpRoutes;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.ViewResolver;
import org.rapidoid.http.handler.HttpHandler;
import org.rapidoid.http.handler.optimized.DelegatingParamsAwareReqHandler;
import org.rapidoid.http.handler.optimized.DelegatingParamsAwareReqRespHandler;
import org.rapidoid.http.impl.AbstractViewResolver;
import org.rapidoid.http.impl.HttpRoutesImpl;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.http.processor.HttpProcessor;
import org.rapidoid.ioc.Beans;
import org.rapidoid.ioc.IoC;
import org.rapidoid.ioc.IoCContext;
import org.rapidoid.job.Jobs;
import org.rapidoid.lambda.NParamLambda;
import org.rapidoid.log.Log;
import org.rapidoid.net.Server;
import org.rapidoid.u.U;
import org.rapidoid.util.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.rapidoid.util.Constants.*;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Setup extends RapidoidInitializer {

	static final Config MAIN_CFG = Msc.isPlatform() ? Conf.RAPIDOID : Conf.ON;
	static final Config ADMIN_CFG = Msc.isPlatform() ? Conf.RAPIDOID_ADMIN : Conf.ADMIN;

	private static final String DEFAULT_ADDRESS = "0.0.0.0";
	private static final int DEFAULT_PORT = Msc.isPlatform() ? 8888 : 8080;

	private static final LazyInit<DefaultSetup> DEFAULT = new LazyInit<>(new Callable<DefaultSetup>() {
		@Override
		public DefaultSetup call() throws Exception {
			return new DefaultSetup();
		}
	});

	static final List<Setup> instances = Coll.synchronizedList();

	static {

		if (Ctxs.getPersisterProvider() == null) {
			Ctxs.setPersisterProvider(new CustomizableSetupAwarePersisterProvider());
		}

		JSON.warmUp();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				shutdownAll();
				Jobs.shutdownNow();
			}
		});
	}

	private final String name;
	private final String zone;
	private final Config serverConfig;

	private final IoCContext ioCContext;

	private final Customization customization;
	private final HttpRoutesImpl routes;
	private volatile RouteOptions defaults = new RouteOptions();

	private volatile Integer port;
	private volatile String address;

	private volatile HttpProcessor processor;

	private volatile boolean listening;
	private volatile Server server;
	private volatile boolean activated;
	private volatile boolean reloaded;
	private volatile boolean autoActivating = true;

	private volatile Runnable onInit;

	private final Once bootstrappedBeans = new Once();

	private final LazyInit<FastHttp> lazyHttp = new LazyInit<>(new Callable<FastHttp>() {
		@Override
		public FastHttp call() throws Exception {
			return initHttp();
		}
	});

	public static Setup create(String name) {
		IoCContext ioc = IoC.createContext().name(name);
		Config config = Conf.section(name);

		Customization customization = new Customization(name, My.custom(), config);
		HttpRoutesImpl routes = new HttpRoutesImpl(name, customization);

		Setup setup = new Setup(name, "main", ioc, config, customization, routes);

		instances.add(setup);
		return setup;
	}

	public void destroy() {
		halt();
		instances.remove(this);
	}

	Setup(String name, String zone, IoCContext ioCContext,
	      Config serverConfig, Customization customization, HttpRoutesImpl routes) {

		this.name = name;
		this.zone = zone;

		this.ioCContext = ioCContext;
		this.serverConfig = serverConfig;
		this.customization = customization;
		this.routes = routes;
		this.defaults.zone(zone);
	}

	static Setup on() {
		return DEFAULT.get().on;
	}

	static Setup admin() {
		return DEFAULT.get().admin;
	}

	private FastHttp initHttp() {
		if (isAdminAndSameAsApp() && on().lazyHttp.isInitialized()) {
			return on().http();

		} else if (isAppAndSameAsAdmin() && admin().lazyHttp.isInitialized()) {
			return admin().http();
		}

		if (isAppOrAdminOnSameServer()) {
			U.must(on().routes == admin().routes);
			return new FastHttp(on().routes, on().serverConfig);
		} else {
			return new FastHttp(routes, serverConfig);
		}
	}

	public FastHttp http() {
		return lazyHttp.get();
	}

	private synchronized Server listen() {

		if (!listening && !reloaded) {

			App.inferCallers();

			listening = true;

			HttpProcessor proc = processor != null ? processor : http();

			if (Env.dev() && !On.changes().isIgnored() && MscOpts.hasRapidoidWatch()) {
				proc = new AppRestartProcessor(this, proc);
				On.changes().byDefaultRestart();
			}

			if (delegateAdminToApp()) {
				server = on().server();

			} else if (delegateAppToAdmin()) {
				server = admin().server();
			}

			if (server == null) {
				int targetPort;

				if (isAppOrAdminOnSameServer()) {
					targetPort = on().port();
					server = proc.listen(on().address(), targetPort);
				} else {
					targetPort = port();
					server = proc.listen(address(), targetPort);
				}

				Log.info("!Server has started", "setup", name(), "!home", "http://localhost:" + targetPort);
				Log.info("!Static resources will be served from the following locations", "setup", name(), "!locations", custom().staticFilesPath());
			}
		}

		return server;
	}

	private boolean isAppOrAdminOnSameServer() {
		return appAndAdminOnSameServer() && (isApp() || isAdmin());
	}

	private boolean delegateAdminToApp() {
		return isAdminAndSameAsApp() && on().server != null;
	}

	private boolean delegateAppToAdmin() {
		return isAppAndSameAsAdmin() && admin().server != null;
	}

	static boolean appAndAdminOnSameServer() {
		String mainPort = MAIN_CFG.entry("port").str().getOrNull();
		String adminPort = ADMIN_CFG.entry("port").str().getOrNull();
		return U.eq(mainPort, adminPort);
	}

	private boolean isAppAndSameAsAdmin() {
		return isApp() && appAndAdminOnSameServer();
	}

	private boolean isAdminAndSameAsApp() {
		return isAdmin() && appAndAdminOnSameServer();
	}

	public boolean isAdmin() {
		return this == admin();
	}

	public boolean isApp() {
		return this == on();
	}

	void autoActivate() {
		if (autoActivating) activate();
	}

	public synchronized void activate() {
		RapidoidEnv.touch();

		if (activated) {
			return;
		}
		activated = true;

		Runnable initializer = onInit;
		if (initializer != null) initializer.run();

		if (!reloaded) {
			listen();
		}

		if (isApp()) {
			AppInfo.isAppServerActive = true;
			AppInfo.appPort = port();
		}

		if (isAdmin()) {
			AppInfo.isAdminServerActive = true;
			AppInfo.adminPort = port();
		}
	}

	public OnRoute route(String verb, String path) {
		return new OnRoute(this, verb.toUpperCase(), path);
	}

	public OnRoute any(String path) {
		return new OnRoute(this, ANY, path);
	}

	public OnRoute get(String path) {
		return new OnRoute(this, GET, path);
	}

	public OnRoute post(String path) {
		return new OnRoute(this, POST, path);
	}

	public OnRoute put(String path) {
		return new OnRoute(this, PUT, path);
	}

	public OnRoute delete(String path) {
		return new OnRoute(this, DELETE, path);
	}

	public OnRoute patch(String path) {
		return new OnRoute(this, PATCH, path);
	}

	public OnRoute options(String path) {
		return new OnRoute(this, OPTIONS, path);
	}

	public OnRoute head(String path) {
		return new OnRoute(this, HEAD, path);
	}

	public OnRoute trace(String path) {
		return new OnRoute(this, TRACE, path);
	}

	public OnRoute page(String path) {
		return new OnRoute(this, GET_OR_POST, path);
	}

	public Setup req(ReqHandler handler) {
		routes.addGenericHandler(new DelegatingParamsAwareReqHandler(http(), routes, opts(), handler));
		autoActivate();
		return this;
	}

	public Setup req(ReqRespHandler handler) {
		routes.addGenericHandler(new DelegatingParamsAwareReqRespHandler(http(), routes, opts(), handler));
		autoActivate();
		return this;
	}

	public Setup req(HttpHandler handler) {
		routes.addGenericHandler(handler);
		autoActivate();
		return this;
	}

	public Setup beans(Object... beans) {
		RapidoidEnv.touch();
		beans = AnyObj.flat(beans);

		for (Object bean : beans) {
			U.notNull(bean, "bean");

			if (bean instanceof NParamLambda) {
				throw U.rte("Expected a bean, but found lambda: " + bean);
			}
		}

		App.filterAndInvokeMainClasses(beans);

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

	public Setup processor(HttpProcessor processor) {
		U.must(!listening, "The server was already initialized!");
		this.processor = processor;
		return this;
	}

	public synchronized Setup shutdown() {
		if (this.server != null) {
			if (this.server.isActive()) {
				this.server.shutdown();
			}
			this.server = null;
		}

		reset();

		return this;
	}

	public synchronized Setup halt() {
		if (this.server != null) {
			if (this.server.isActive()) {
				this.server.halt();
			}
			this.server = null;
		}

		reset();

		return this;
	}

	public void reset() {
		http().resetConfig();
		listening = false;
		reloaded = false;
		port = null;
		lazyHttp.reset();
		address = null;
		processor = null;
		activated = false;
		ioCContext.reset();
		server = null;
		autoActivating = true;

		defaults = new RouteOptions();
		defaults().zone(zone);

		if (isApp()) {
			AppInfo.isAppServerActive = false;
			AppInfo.appPort = 0;
		}

		if (isAdmin()) {
			AppInfo.isAdminServerActive = false;
			AppInfo.adminPort = 0;
		}

		bootstrappedBeans.reset();

		initDefaults();
	}

	public Server server() {
		return server;
	}

	public Map<String, Object> attributes() {
		return http().attributes();
	}

	@SuppressWarnings("unchecked")
	public Setup scan(String... packages) {
		if (!bootstrappedBeans.go()) return this;

		beans(App.findBeans(packages).toArray());

		return this;
	}

	public Setup deregister(String verb, String path) {
		routes.remove(verb, path);
		return this;
	}

	public Setup deregister(Object... controllers) {
		PojoHandlersSetup.from(this, controllers).deregister();
		return this;
	}

	public IoCContext context() {
		return ioCContext;
	}

	public void reload() {
		reloaded = true;
		bootstrappedBeans.reset();
		ioCContext.reset();
		http().resetConfig();
		defaults = new RouteOptions();
		defaults.zone(zone);
		attributes().clear();

		ViewResolver viewResolver = custom().viewResolver();
		if (viewResolver instanceof AbstractViewResolver) {
			((AbstractViewResolver) viewResolver).reset();
		}
	}

	public static List<Setup> instances() {
		return Collections.unmodifiableList(instances);
	}

	public Config config() {
		return serverConfig;
	}

	public Customization custom() {
		return customization;
	}

	public HttpRoutes routes() {
		return routes;
	}

	private RouteOptions opts() {
		return new RouteOptions();
	}

	public String name() {
		return name;
	}

	public RouteOptions defaults() {
		return defaults;
	}

	public String zone() {
		return zone;
	}

	public boolean isRunning() {
		return activated;
	}

	public static synchronized void haltAll() {
		for (Setup setup : instances()) {
			setup.halt();
		}
	}

	public static synchronized void shutdownAll() {
		for (Setup setup : instances()) {
			setup.shutdown();
		}
	}

	public static synchronized boolean isAnyRunning() {
		for (Setup setup : instances()) {
			if (setup.isRunning()) return true;
		}

		return false;
	}

	public int port() {
		if (port == null) {
			port = serverConfig.entry("port").or(DEFAULT_PORT);
		}

		U.must(port >= 0, "The port of server setup '%s' is negative!", name());

		return port;
	}

	public String address() {
		if (address == null) {
			address = serverConfig.entry("address").or(DEFAULT_ADDRESS);
		}

		U.must(U.notEmpty(address), "The address of server setup '%s' is empty!", name());

		return address;
	}

	public OnError error(Class<? extends Throwable> error) {
		return new OnError(customization, error);
	}

	public void register(Beans beans) {
		beans(beans.getAnnotated(U.set(IoC.ANNOTATIONS)));
	}

	static void initDefaults() {
		DefaultSetup defaultSetup = DEFAULT.getValue();

		if (defaultSetup != null) {
			defaultSetup.initDefaults();
		}
	}

	@Override
	public String toString() {
		return "Setup{" +
			"name='" + name + '\'' +
			", zone='" + zone + '\'' +
			", serverConfig=" + serverConfig +
			", customization=" + customization +
			", routes=" + routes +
			'}';
	}

	public void onInit(Runnable onInit) {
		this.onInit = onInit;
	}

	public boolean autoActivating() {
		return autoActivating;
	}

	public Setup autoActivating(boolean autoActivating) {
		this.autoActivating = autoActivating;
		return this;
	}
}
