package org.rapidoid.goodies;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.security.Roles;
import org.rapidoid.setup.Admin;
import org.rapidoid.setup.Dev;
import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;
import org.rapidoid.util.UTILS;

import java.lang.management.ManagementFactory;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Goodies {

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
		return new DetailsHandler("Threads", ManagementFactory.getThreadMXBean(), "-objectName", "-allThreadIds");
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

	private static ConfigHandler config() {
		return new ConfigHandler();
	}

	private static RoutesHandler routes() {
		return new RoutesHandler();
	}

	public static void bootstrap(Setup setup) {

		if (!setup.goodies()) {
			Log.warn("Goodies are disabled for setup: " + setup.name());
			return;
		}

		if (setup == On.setup()) {
			UTILS.logSection("Registering App goodies:");
		}

		if (setup == Dev.setup()) {
			UTILS.logSection("Registering Dev goodies:");

			setup.page("/").mvc(Goodies.routes());
			setup.page("/metrics").mvc(Goodies.graphs());
			setup.page("/config").mvc(Goodies.config());

			setup.get("/_graphs/{id:.*}").json(Goodies.graphData());
		}

		if (setup == Admin.setup()) {
			UTILS.logSection("Registering Admin goodies:");

			setup.page("/").mvc(Goodies.graphs());
			setup.page("/routes").mvc(Goodies.routes());

			setup.page("/jmx/memory").mvc(Goodies.memory());
			setup.page("/jmx/mempool").mvc(Goodies.memoryPool());
			setup.page("/jmx/classes").mvc(Goodies.classes());
			setup.page("/jmx/os").mvc(Goodies.os());
			setup.page("/jmx/threads").mvc(Goodies.threads());
			setup.page("/jmx/compilation").mvc(Goodies.compilation());
			setup.page("/jmx/runtime").mvc(Goodies.runtime());
			setup.page("/jmx/gc").mvc(Goodies.gc());

			setup.get("/_graphs/{id:.*}").json(Goodies.graphData());
		}

		setup.post("/_login").roles().json(Goodies.login());
		setup.get("/_logout").roles(Roles.LOGGED_IN).json(Goodies.logout());
	}

}
