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

			setup.page("/").render(Goodies.routes());
			setup.page("/config").render(Goodies.config());
		}

		if (setup == Admin.setup()) {
			UTILS.logSection("Registering Admin goodies:");

			setup.page("/").render(Goodies.graphs());
			setup.page("/routes").render(Goodies.routes());

			setup.page("/jmx/memory").render(Goodies.memory());
			setup.page("/jmx/mempool").render(Goodies.memoryPool());
			setup.page("/jmx/classes").render(Goodies.classes());
			setup.page("/jmx/os").render(Goodies.os());
			setup.page("/jmx/threads").render(Goodies.threads());
			setup.page("/jmx/compilation").render(Goodies.compilation());
			setup.page("/jmx/runtime").render(Goodies.runtime());
			setup.page("/jmx/gc").render(Goodies.gc());
		}

		setup.post("/_login").json(Goodies.login());
		setup.get("/_logout").json(Goodies.logout());
	}

}
