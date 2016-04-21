package org.rapidoid.goodies;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.jpa.JPA;
import org.rapidoid.log.Log;
import org.rapidoid.security.Roles;
import org.rapidoid.setup.Admin;
import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;
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
		return new DetailsHandler("JVM Threads", ManagementFactory.getThreadMXBean(), "-objectName", "-allThreadIds");
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
		return new DetailsHandler("Memory", ManagementFactory.getMemoryMXBean(), "-objectName");
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

	public static DataHandler data() {
		return new DataHandler();
	}

	public static RoutesHandler routes() {
		return new RoutesHandler();
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

	public static void bootstrap(Setup setup) {

		if (!setup.goodies()) {
			Log.warn("Goodies are disabled for setup: " + setup.name());
			return;
		}

		if (setup == On.setup()) {
			Msc.logSection("Registering App goodies:");
		}

		if (setup == Admin.setup()) {
			Msc.logSection("Registering Admin goodies:");

			setup.page("/_/").mvc(Goodies.overview());

			if (Msc.hasJPA()) {
				jpaGoodies(setup);
			}

			setup.page("/_/routes").mvc(Goodies.routes());
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
			setup.get("/_/deploy").mvc(Goodies.deploy());
			setup.post("/_/upload-jar").json(Goodies.jarUpload());
		}

		setup.post("/_login").roles().json(Goodies.login());
		setup.get("/_logout").roles(Roles.LOGGED_IN).json(Goodies.logout());
	}

	private static void jpaGoodies(Setup setup) {
		setup.page("/_/data").mvc(Goodies.data());

		for (Class<?> type : JPA.getEntityJavaTypes()) {
			String uri = GUI.typeUri(type);
			String contextPath = HttpUtils.getContextPath(setup.custom(), setup.segment());
			X.scaffold(setup, Msc.uri(contextPath, uri), type);
		}
	}

}
