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
import org.rapidoid.http.*;
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
import org.rapidoid.util.AppInfo;
import org.rapidoid.util.LazyInit;
import org.rapidoid.util.MscOpts;
import org.rapidoid.util.Once;

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

	private final String defaultAddress;
	private final int defaultPort;

	private final IoCContext ioCContext;

	private final Customization customization;
	private final HttpRoutesImpl routes;
	private volatile FastHttp http;
	private volatile RouteOptions defaults = new RouteOptions();

	private volatile Integer port;
	private volatile String address;

	private volatile HttpProcessor processor;

	private volatile boolean listening;
	private volatile Server server;
	private volatile boolean activated;
	private volatile boolean reloaded;

	private final Once bootstrapedComponents = new Once();

	public static Setup create(String name) {
		IoCContext ioc = IoC.createContext().name(name);
		Config config = Conf.section(name);

		Customization customization = new Customization(name, My.custom(), config);
		HttpRoutesImpl routes = new HttpRoutesImpl(customization);

		Setup setup = new Setup(name, "main", "0.0.0.0", 8080, ioc, config, customization, routes);

		instances.add(setup);
		return setup;
	}

	public void destroy() {
		halt();
		instances.remove(this);
	}

	Setup(String name, String zone, String defaultAddress, int defaultPort, IoCContext ioCContext, Config serverConfig, Customization customization, HttpRoutesImpl routes) {
		this.name = name;
		this.zone = zone;

		this.defaultAddress = defaultAddress;
		this.defaultPort = defaultPort;

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

	public FastHttp http() {
		if (http != null) {
			return http;
		}

		synchronized (this) {
			if (isAdminAndSameAsApp() && on().http != null) {
				return on().http;

			} else if (isAppAndSameAsAdmin() && admin().http != null) {
				return admin().http;
			}

			if (http == null) {
				if (isAppOrAdminOnSameServer()) {
					U.must(on().routes == admin().routes);
					http = new FastHttp(new HttpRoutesGroup(on().routes), on().serverConfig);
				} else {
					http = new FastHttp(new HttpRoutesGroup(routes), serverConfig);
				}
			}
		}

		return http;
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
				int onPort;

				if (isAppOrAdminOnSameServer()) {
					onPort = on().port();
					server = proc.listen(on().address(), onPort);
				} else {
					onPort = port();
					server = proc.listen(address(), onPort);
				}

				Log.info("!Server has started", "setup", name(), "!home", "http://localhost:" + onPort);
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
		return U.eq(Conf.ADMIN.get("port"), "same");
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

	public synchronized void activate() {
		RapidoidEnv.touch();

		if (activated) {
			return;
		}
		activated = true;

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
		activate();
		return new OnRoute(http(), defaults, routes, verb.toUpperCase(), path);
	}

	public OnRoute any(String path) {
		activate();
		return new OnRoute(http(), defaults, routes, ANY, path);
	}

	public OnRoute get(String path) {
		activate();
		return new OnRoute(http(), defaults, routes, GET, path);
	}

	public OnRoute post(String path) {
		activate();
		return new OnRoute(http(), defaults, routes, POST, path);
	}

	public OnRoute put(String path) {
		activate();
		return new OnRoute(http(), defaults, routes, PUT, path);
	}

	public OnRoute delete(String path) {
		activate();
		return new OnRoute(http(), defaults, routes, DELETE, path);
	}

	public OnRoute patch(String path) {
		activate();
		return new OnRoute(http(), defaults, routes, PATCH, path);
	}

	public OnRoute options(String path) {
		activate();
		return new OnRoute(http(), defaults, routes, OPTIONS, path);
	}

	public OnRoute head(String path) {
		activate();
		return new OnRoute(http(), defaults, routes, HEAD, path);
	}

	public OnRoute trace(String path) {
		activate();
		return new OnRoute(http(), defaults, routes, TRACE, path);
	}

	public OnRoute page(String path) {
		activate();
		return new OnRoute(http(), defaults, routes, GET_OR_POST, path);
	}

	public Setup req(ReqHandler handler) {
		activate();
		routes.addGenericHandler(new DelegatingParamsAwareReqHandler(http(), routes, opts(), handler));
		return this;
	}

	public Setup req(ReqRespHandler handler) {
		activate();
		routes.addGenericHandler(new DelegatingParamsAwareReqRespHandler(http(), routes, opts(), handler));
		return this;
	}

	public Setup req(HttpHandler handler) {
		activate();
		routes.addGenericHandler(handler);
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
		http = null;
		address = null;
		processor = null;
		activated = false;
		ioCContext.reset();
		server = null;

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

		bootstrapedComponents.reset();

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
		if (!bootstrapedComponents.go()) return this;

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
		bootstrapedComponents.reset();
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
			port = calcPort();
		}

		port = U.or(port, defaultPort);

		U.must(port >= 0, "The port of server setup '%s' is negative!", name());
		return port;
	}

	private Integer calcPort() {
		if (port != null) {
			return port;
		}

		String portCfg = serverConfig.entry("port").str().getOrNull();

		if (U.notEmpty(portCfg)) {
			if (portCfg.equalsIgnoreCase("same")) {
				U.must(!isApp(), "Cannot configure the app port (on.port) with value = 'same'!");
				return on().port();

			} else {
				return U.num(portCfg);
			}
		}

		return null;
	}

	public String address() {
		if (address == null) {
			address = serverConfig.entry("address").or(defaultAddress);
		}

		return address;
	}

	public OnError error(Class<? extends Throwable> error) {
		return new OnError(customization, error);
	}

	public void register(Beans beans) {
		beans(beans.getAnnotated(U.set(IoC.ANNOTATIONS)));
	}

	static void initDefaults() {
		DEFAULT.get().initDefaults();
	}
}
