package org.rapidoid.goodies;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Env;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.insight.Metrics;
import org.rapidoid.jpa.JPA;
import org.rapidoid.log.Log;
import org.rapidoid.security.Role;
import org.rapidoid.setup.Setup;
import org.rapidoid.util.AnsiColor;
import org.rapidoid.util.Msc;

import java.lang.management.ManagementFactory;

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

	private static volatile String generatedAdminPassword;

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
		if (!setup.goodies()) {
			Log.warn("Goodies are disabled for setup: " + setup.name());
			return;
		}

		Msc.logSection("Registering App goodies:");

		auth(setup);
	}

	public static void adminCenter(Setup setup) {
		if (!setup.goodies()) {
			Log.warn("Goodies are disabled for setup: " + setup.name());
			return;
		}

		Msc.logSection("Registering Admin goodies:");

		Metrics.bootstrap();

		setup.page("/_").mvc(Goodies.overview());

		jpaGoodies(setup);

		setup.page("/_/routes").mvc(Goodies.routes());
		setup.page("/_/beans").mvc(Goodies.beans());
		setup.page("/_/config").mvc(Goodies.config());

		setup.page("/_/jmx/memory").mvc(Goodies.memory());
		setup.page("/_/jmx/mempool").mvc(Goodies.memoryPool());
		setup.page("/_/jmx/classes").mvc(Goodies.classes());
		setup.page("/_/jmx/os").mvc(Goodies.os());
		setup.page("/_/jmx/threads").mvc(Goodies.threads());
		setup.page("/_/jmx/compilation").mvc(Goodies.compilation());
		setup.page("/_/jmx/runtime").mvc(Goodies.runtime());
		setup.page("/_/jmx/gc").mvc(Goodies.gc());

		setup.page("/_/metrics").mvc(Goodies.graphs());
		setup.get("/_/graphs/{id:.*}").json(Goodies.graphData());

		setup.get("/_/classpath").mvc(Goodies.classpath());
		setup.page("/_/deploy").mvc(Goodies.deploy());
		setup.post("/_/jar").json(Goodies.jarUpload());
		setup.page("/_/terminate").mvc(Goodies.terminate());

		setup.get("/_ping").plain("OK");

		auth(setup);

		if (Conf.USERS.isEmpty() && Env.dev()) {
			String pass = generatedAdminPassword();
			Config admin = Conf.USERS.sub("admin");
			admin.set("roles", "administrator");
			admin.set("password", pass);
			Msc.logSection("ADMIN CREDENTIALS: username = " + AnsiColor.bold("admin") + ", password = " + AnsiColor.bold(pass));
		}
	}

	public static synchronized String generatedAdminPassword() {
		if (generatedAdminPassword == null) {
			generatedAdminPassword = Crypto.randomStr(16);
		}

		return generatedAdminPassword;
	}

	private static void jpaGoodies(Setup setup) {
		setup.page("/_/entities").mvc(Goodies.entities());

		if (Msc.hasJPA()) {
			for (Class<?> type : JPA.getEntityJavaTypes()) {
				String uri = GUI.typeUri(type);
				String contextPath = HttpUtils.getContextPath(setup.custom(), setup.segment());
				X.scaffold(setup, Msc.uri(contextPath, uri), type);
			}
		}
	}

}
