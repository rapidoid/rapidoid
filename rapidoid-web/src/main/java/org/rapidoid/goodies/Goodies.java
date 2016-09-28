package org.rapidoid.goodies;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.insight.Metrics;
import org.rapidoid.jpa.JPA;
import org.rapidoid.security.Role;
import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

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
public class Goodies extends RapidoidThing {

	public static MultiDetailsHandler memoryPool() {
		return new MultiDetailsHandler("Memory pool", ManagementFactory.getMemoryPoolMXBeans(), "name", "type", "memoryManagerNames", "usage", "peakUsage", "collectionUsage");
	}

	public static DetailsHandler classes() {
		return new DetailsHandler("Classes", ManagementFactory.getClassLoadingMXBean(), "-objectName");
	}

	public static DetailsHandler os() {
		return new DetailsHandler("Operating system", ManagementFactory.getOperatingSystemMXBean(), "-objectName");
	}

	public static DetailsHandler threads() {
		return new DetailsHandler("JVM Threads", ManagementFactory.getThreadMXBean(), "-objectName", "-allThreadIds").sorted(true);
	}

	public static DetailsHandler compilation() {
		return new DetailsHandler("Compilation", ManagementFactory.getCompilationMXBean(), "-objectName");
	}

	public static DetailsHandler runtime() {
		return new DetailsHandler("Runtime", ManagementFactory.getRuntimeMXBean(), "-objectName", "-classPath", "-bootClassPath", "-systemProperties");
	}

	public static MultiDetailsHandler gc() {
		return new MultiDetailsHandler("Garbage collection", ManagementFactory.getGarbageCollectorMXBeans(), "-objectName", "-memoryPools", "-lastGcInfo");
	}

	public static DetailsHandler memory() {
		return new DetailsHandler("Memory", ManagementFactory.getMemoryMXBean(), "-objectName").sorted(true);
	}

	public static GraphsHandler graphs() {
		return new GraphsHandler();
	}

	public static GraphDataHandler graphData() {
		return new GraphDataHandler();
	}

	public static LoginHandler login() {
		return new LoginHandler();
	}

	public static LogoutHandler logout() {
		return new LogoutHandler();
	}

	public static ConfigHandler config() {
		return new ConfigHandler();
	}

	public static EntitiesHandler entities() {
		return new EntitiesHandler();
	}

	public static RoutesHandler routes() {
		return new RoutesHandler();
	}

	public static BeansHandler beans() {
		return new BeansHandler();
	}

	public static OverviewHandler overview() {
		return new OverviewHandler();
	}

	public static ClasspathHandler classpath() {
		return new ClasspathHandler();
	}

	public static DeployHandler deploy() {
		return new DeployHandler();
	}

	public static JarUploadHandler jarUpload() {
		return new JarUploadHandler();
	}

	public static TerminateHandler terminate() {
		return new TerminateHandler();
	}

	public static StatusHandler status() {
		return new StatusHandler();
	}

	public static void bootstrap(Setup setup) {
		if (setup.isAdmin()) {
			adminCenter(setup);
		} else if (setup.isApp()) {
			bootstrapAppGoodies(setup);
		}
	}

	public static void auth(Setup setup) {
		setup.post("/_login").roles().json(Goodies.login());
		setup.get("/_logout").roles(Role.LOGGED_IN).json(Goodies.logout());
	}

	public static void bootstrapAppGoodies(Setup setup) {
		Msc.logSection("Registering App services:");

		auth(setup);
	}

	public static void adminCenter(Setup setup) {
		Msc.logSection("Registering Admin Center:");

		Metrics.bootstrap();

		overview(setup);
		entities(setup);
		application(setup);
		lifecycle(setup);
		jmx(setup);
		metrics(setup);
		deploy(setup);
		auth(setup);
		status(setup);
	}

	public static void ping(Setup setup) {
		setup.get("/_ping").plain("OK");
	}

	public static void lifecycle(Setup setup) {
		setup.page("/_/terminate").mvc(Goodies.terminate());
	}

	public static void overview(Setup setup) {
		setup.page("/_").mvc(Goodies.overview());
	}

	public static void application(Setup setup) {
		setup.page("/_/routes").mvc(Goodies.routes());
		setup.page("/_/beans").mvc(Goodies.beans());
		setup.page("/_/config").mvc(Goodies.config());
		setup.get("/_/classpath").mvc(Goodies.classpath());
	}

	public static void deploy(Setup setup) {
		setup.page("/_/deploy").mvc(Goodies.deploy());
		setup.post("/_/jar").json(Goodies.jarUpload());
	}

	public static void metrics(Setup setup) {
		setup.page("/_/metrics").mvc(Goodies.graphs());
		setup.get("/_/graphs/{id:.*}").json(Goodies.graphData());
	}

	public static void jmx(Setup setup) {
		setup.page("/_/jmx/memory").mvc(Goodies.memory());
		setup.page("/_/jmx/mempool").mvc(Goodies.memoryPool());
		setup.page("/_/jmx/classes").mvc(Goodies.classes());
		setup.page("/_/jmx/os").mvc(Goodies.os());
		setup.page("/_/jmx/threads").mvc(Goodies.threads());
		setup.page("/_/jmx/compilation").mvc(Goodies.compilation());
		setup.page("/_/jmx/runtime").mvc(Goodies.runtime());
		setup.page("/_/jmx/gc").mvc(Goodies.gc());
	}

	public static void entities(Setup setup) {
		setup.page("/_/entities").mvc(Goodies.entities());

		if (Msc.hasJPA()) {
			for (Class<?> type : JPA.getEntityJavaTypes()) {
				String uri = GUI.typeUri(type);
				String contextPath = HttpUtils.zone(setup.custom(), setup.zone()).entry("home").or("/_");
				X.scaffold(setup, Msc.uri(contextPath, uri), type);
			}
		}
	}

	public static void oauth(Setup setup) {
		Class<?> oauthClass = Cls.getClassIfExists("org.rapidoid.oauth.OAuth");
		U.must(oauthClass != null, "Cannot find the OAuth components, is module 'rapidoid-oauth' missing?");

		Method bootstrap = Cls.getMethod(oauthClass, "bootstrap", Setup.class);

		Cls.invokeStatic(bootstrap, setup);
	}

	public static void welcome(Setup setup) {
		if (!setup.routes().hasRouteOrResource(HttpVerb.GET, "/")) {
			On.get("/").view("_welcome").mvc(welcome());
		}
	}

	public static ReqRespHandler welcome() {
		return new WelcomeHandler();
	}

	public static void status(Setup setup) {
		setup.get("/_status").json(Goodies.status());
	}

}
