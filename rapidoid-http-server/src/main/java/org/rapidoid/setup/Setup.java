package org.rapidoid.setup;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.*;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Coll;
import org.rapidoid.commons.Env;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.data.JSON;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.HttpRoutes;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.handler.HttpHandler;
import org.rapidoid.http.handler.optimized.DelegatingParamsAwareReqHandler;
import org.rapidoid.http.handler.optimized.DelegatingParamsAwareReqRespHandler;
import org.rapidoid.http.impl.HttpRoutesImpl;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.http.processor.HttpProcessor;
import org.rapidoid.ioc.IoC;
import org.rapidoid.ioc.IoCContext;
import org.rapidoid.job.Jobs;
import org.rapidoid.jpa.JPA;
import org.rapidoid.jpa.JPAPersisterProvider;
import org.rapidoid.lambda.NParamLambda;
import org.rapidoid.log.Log;
import org.rapidoid.net.Server;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.scan.Scan;
import org.rapidoid.security.Roles;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscInfo;
import org.rapidoid.util.Once;

import java.lang.annotation.Annotation;
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
public class Setup extends RapidoidThing implements Constants {

	static final Setup ON = new Setup("app", "main", "0.0.0.0", 8888, IoC.defaultContext(), Conf.APP, Conf.ON);
	static final Setup ADMIN = new Setup("admin", "admin", "0.0.0.0", 0, IoC.defaultContext(), Conf.APP, Conf.ADMIN);

	private static final List<Setup> instances = Coll.synchronizedList(ON, ADMIN);

	static {
		RapidoidInitializer.initialize();

		Jobs.execute(new Runnable() {
			@Override
			public void run() {
				JSON.warmup();
			}
		});

		if (Ctxs.getPersisterProvider() == null) {
			Ctxs.setPersisterProvider(new JPAPersisterProvider());
		}
	}

	private final String name;
	private final String segment;
	private final Config appConfig;
	private final Config serverConfig;

	private final String defaultAddress;
	private final int defaultPort;

	private final IoCContext ioCContext;

	private final Customization customization;
	private final HttpRoutesImpl routes;
	private final FastHttp http;
	private volatile RouteOptions defaults = new RouteOptions();

	private volatile Integer port;
	private volatile String address;

	private volatile HttpProcessor processor;

	private volatile boolean listening;
	private volatile Server server;
	private volatile boolean activated;
	private volatile boolean reloaded;
	private volatile boolean goodies = true;

	private final Once bootstrapedComponents = new Once();
	private final Once bootstrapedJPA = new Once();
	private final Once bootstrapedGoodies = new Once();

	public static Setup create(String name) {
		IoCContext ioc = IoC.createContext().name(name);
		Config config = Conf.section(name);
		Setup setup = new Setup(name, "main", "0.0.0.0", 8888, ioc, config, config);
		instances.add(setup);
		return setup;
	}

	public void destroy() {
		halt();
		instances.remove(this);
	}

	private Setup(String name, String segment, String defaultAddress, int defaultPort, IoCContext ioCContext, Config appConfig, Config serverConfig) {
		this.name = name;
		this.segment = segment;

		this.defaultAddress = defaultAddress;
		this.defaultPort = defaultPort;

		this.ioCContext = ioCContext;

		this.appConfig = appConfig;
		this.serverConfig = serverConfig;

		this.customization = new Customization(name, appConfig, serverConfig);
		this.routes = new HttpRoutesImpl(customization);
		this.http = new FastHttp(routes);

		this.defaults.segment(segment);
	}

	public FastHttp http() {
		return delegateAdminToApp() ? ON.http() : http;
	}

	public synchronized Server listen() {
		if (!listening && !reloaded) {

			App.inferCallers();

			listening = true;

			HttpProcessor proc = processor != null ? processor : http();

			if (Env.dev() && !OnChanges.isIgnored()) {
				proc = new AppRestartProcessor(this, proc);
				OnChanges.byDefaultRestart();
			}

			if (delegateAdminToApp()) {
				server = ON.server();

			} else if (delegateAppToAdmin()) {
				server = ADMIN.server();
			}

			if (server == null) {
				if (appAndAdminOnSameServer()) {
					server = proc.listen(ON.address(), ON.port());
				} else {
					server = proc.listen(address(), port());
				}
			}
		}

		return server;
	}

	private boolean delegateAdminToApp() {
		return isAdmin() && appAndAdminOnSameServer();
	}

	private boolean delegateAppToAdmin() {
		return isApp() && appAndAdminOnSameServer();
	}

	private boolean appAndAdminOnSameServer() {
		return Conf.ADMIN.entry("port").or(0) == 0;
	}

	public boolean isAdmin() {
		return this == ADMIN;
	}

	public boolean isApp() {
		return this == ON;
	}

	private synchronized void activate() {
		if (activated) {
			return;
		}
		activated = true;

		if (!reloaded) {
			listen();
		}

		if (isAdmin()) {
			MscInfo.isAdminActive = true;
		}
	}

	public OnRoute route(String verb, String path) {
		activate();
		return new OnRoute(http(), defaults, verb.toUpperCase(), path);
	}

	public OnRoute get(String path) {
		activate();
		return new OnRoute(http(), defaults, GET, path);
	}

	public OnRoute post(String path) {
		activate();
		return new OnRoute(http(), defaults, POST, path);
	}

	public OnRoute put(String path) {
		activate();
		return new OnRoute(http(), defaults, PUT, path);
	}

	public OnRoute delete(String path) {
		activate();
		return new OnRoute(http(), defaults, DELETE, path);
	}

	public OnRoute patch(String path) {
		activate();
		return new OnRoute(http(), defaults, PATCH, path);
	}

	public OnRoute options(String path) {
		activate();
		return new OnRoute(http(), defaults, OPTIONS, path);
	}

	public OnRoute head(String path) {
		activate();
		return new OnRoute(http(), defaults, HEAD, path);
	}

	public OnRoute trace(String path) {
		activate();
		return new OnRoute(http(), defaults, TRACE, path);
	}

	public OnRoute page(String path) {
		activate();
		return new OnRoute(http(), defaults, GET_OR_POST, path);
	}

	public Setup req(ReqHandler handler) {
		activate();
		routes.addGenericHandler(new DelegatingParamsAwareReqHandler(http(), opts(), handler));
		return this;
	}

	public Setup req(ReqRespHandler handler) {
		activate();
		routes.addGenericHandler(new DelegatingParamsAwareReqRespHandler(http(), opts(), handler));
		return this;
	}

	public Setup req(HttpHandler handler) {
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

		invokeMainComponents(beans);

		return this;
	}

	private void invokeMainComponents(Object[] beans) {
		for (Object bean : beans) {
			if (bean instanceof Class<?>) {
				Class<?> clazz = (Class<?>) bean;
				if (Cls.isAnnotated(clazz, Main.class)) {
					Msc.logSection("Invoking @Main component: " + clazz.getName());
					Msc.invokeMain(clazz, Conf.getArgs());
				}
			}
		}
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

	public Setup shutdown() {
		reset();
		if (this.server != null && !delegateAdminToApp()) {
			this.server.shutdown();
			this.server = null;
		}
		return this;
	}

	public Setup halt() {
		reset();
		if (this.server != null && !delegateAdminToApp()) {
			this.server.halt();
			this.server = null;
		}
		return this;
	}

	public void reset() {
		http().resetConfig();
		listening = false;
		reloaded = false;
		port = null;
		address = null;
		processor = null;
		activated = false;
		ioCContext.reset();
		goodies = true;
		defaults = new RouteOptions();
		defaults().segment(segment);

		if (isAdmin()) {
			MscInfo.isAdminActive = false;
		}

		bootstrapedJPA.reset();
		bootstrapedComponents.reset();
		bootstrapedGoodies.reset();
	}

	public Server server() {
		return server;
	}

	public Map<String, Object> attributes() {
		return http().attributes();
	}

	public Setup bootstrap() {
		Log.setStyled(Env.dev());

		setupConfig();

		if (!isAdmin()) {
			bootstrapJPA();
			scan();
		}

		bootstrapGoodies();

		Log.info("Completed bootstrap", "IoC context", iocContext());
		return this;
	}

	private void setupConfig() {
		String appJar = Conf.APP.entry("jar").str().getOrNull();
		if (U.notEmpty(appJar)) {
			ClasspathUtil.appJar(appJar);
		}
	}

	public Setup bootstrapJPA() {
		if (!bootstrapedJPA.go()) return this;

		if (Msc.hasJPA()) {
			JPA.bootstrap(App.path());
		}

		return this;
	}

	@SuppressWarnings("unchecked")
	public Setup scan(String... packages) {
		if (!bootstrapedComponents.go()) return this;

		List<Class<? extends Annotation>> annotated = U.list(Controller.class, Service.class, Main.class);

		if (Msc.hasInject()) {
			annotated.add(Cls.<Annotation>get("javax.inject.Named"));
			annotated.add(Cls.<Annotation>get("javax.inject.Singleton"));
		}

		if (U.isEmpty(packages)) {
			packages = App.path();
		}

		beans(Scan.annotated(annotated).in(packages).loadAll().toArray());
		return this;
	}

	public Setup bootstrapGoodies() {
		if (!bootstrapedGoodies.go()) return this;

		Class<?> goodiesClass = Cls.getClassIfExists("org.rapidoid.goodies.RapidoidGoodiesModule");

		if (goodiesClass != null) Cls.newInstance(goodiesClass, this);

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

	public IoCContext iocContext() {
		return ioCContext;
	}

	public void reload() {
		reloaded = true;

		bootstrapedJPA.reset();
		bootstrapedComponents.reset();
		bootstrapedGoodies.reset();

		ioCContext.reset();
		http().resetConfig();
		defaults = new RouteOptions();
		defaults.segment(segment);
		attributes().clear();
		initDefaults();
	}

	static void initDefaults() {
		ADMIN.defaults().roles(Roles.ADMINISTRATOR);
	}

	public static List<Setup> instances() {
		return instances;
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

	public boolean goodies() {
		return goodies;
	}

	public Setup goodies(boolean goodies) {
		this.goodies = goodies;
		return this;
	}

	public String name() {
		return name;
	}

	public RouteOptions defaults() {
		return defaults;
	}

	public String segment() {
		return segment;
	}

	public boolean isActive() {
		return activated;
	}

	public static void haltAll() {
		for (Setup setup : instances()) {
			setup.halt();
		}

		System.exit(0);
	}

	public static void shutdownAll() {
		for (Setup setup : instances()) {
			setup.shutdown();
		}

		System.exit(0);
	}

	public int port() {
		if (port == null) {
			port = serverConfig.entry("port").or(defaultPort);
		}

		return port;
	}

	public String address() {
		if (address == null) {
			address = serverConfig.entry("address").or(defaultAddress);
		}

		return address;
	}
}
