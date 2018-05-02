/*-
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.goodies;

import org.rapidoid.ModuleBootstrapParams;
import org.rapidoid.RapidoidModules;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.BasicConfig;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.insight.Metrics;
import org.rapidoid.jpa.JPA;
import org.rapidoid.security.Role;
import org.rapidoid.setup.App;
import org.rapidoid.setup.Setup;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscOpts;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
@SuppressWarnings("WeakerAccess")
public class Boot extends RapidoidThing {

	public static final JMXGoodies JMX = new JMXGoodies();

	public static final String CENTER = "center";

	public static void adminCenter(Setup setup) {
		Msc.logSection("Registering Admin Center:");

		overview(setup);
		entities(setup);
		application(setup);
		lifecycle(setup);
		jmx(setup);
		metrics(setup);
		auth(setup);
	}

	public static void auth(Setup setup) {
		setup.post(uri("login"))
			.roles()
			.json(new LoginHandler());

		setup.get(uri("logout"))
			.roles(Role.LOGGED_IN)
			.json(new LogoutHandler());
	}

	public static void lifecycle(Setup setup) {
		setup.page(uri("terminate"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("System", "Terminate / Restart")
			.mvc(new TerminateHandler());
	}

	public static void overview(Setup setup) {
		setup.page(uri(""))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("System", "Overview")
			.mvc(new OverviewHandler());
	}

	public static void application(Setup setup) {
		setup.page(uri("routes"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("Routes")
			.mvc(new RoutesHandler());

		setup.page(uri("config"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("Configuration")
			.mvc(new ConfigHandler());

		setup.page(uri("beans"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("Application", "Beans")
			.mvc(new BeansHandler());

		setup.get(uri("classpath"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("Application", "Classpath")
			.mvc(new ClasspathHandler());
	}

	public static void metrics(Setup setup) {
		Metrics.bootstrap();

		setup.page(uri("metrics"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("Metrics")
			.mvc(new GraphsHandler());

		setup.get(uri("graphs/{id:.*}"))
			.roles(Role.ADMINISTRATOR)
			.json(new GraphDataHandler());
	}

	public static void jmx(Setup setup) {
		setup.page(uri("jmx/mempool"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("System", "Memory pool")
			.mvc(JMX.memoryPool());

		setup.page(uri("jmx/threads"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("System", "JVM Threads")
			.mvc(JMX.threads());

		setup.page(uri("jmx/os"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("System", "Operating system")
			.mvc(JMX.os());

		setup.page(uri("jmx/gc"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("System", "Garbage collection")
			.mvc(JMX.gc());

		setup.page(uri("jmx/memory"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("System", "Memory")
			.mvc(JMX.memory());

		setup.page(uri("jmx/runtime"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("System", "Runtime")
			.mvc(JMX.runtime());

		setup.page(uri("jmx/classes"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("System", "Classes")
			.mvc(JMX.classes());

		setup.page(uri("jmx/compilation"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("System", "Compilation")
			.mvc(JMX.compilation());
	}

	public static void entities(Setup setup) {
		setup.page(uri("entities"))
			.roles(Role.ADMINISTRATOR)
			.zone(CENTER)
			.menu("System", "Entities")
			.mvc(new EntitiesHandler());

		if (MscOpts.hasJPA()) {
			for (Class<?> type : JPA.getEntityJavaTypes()) {
				String uri = GUI.typeUri(type);

				BasicConfig zone = HttpUtils.zone(setup.custom(), setup.zone());
				String contextPath = zone.entry("home").or(uri(""));

				X.scaffold(type)
					.baseUri(Msc.uri(contextPath, uri))
					.roles(Role.ADMINISTRATOR)
					.on(setup);
			}
		}
	}

	public static void oauth(Setup setup) {
		ModuleBootstrapParams params = new ModuleBootstrapParams().setup(setup);
		RapidoidModules.get("OAuth").bootstrap(params);
	}

	public static void openapi(Setup setup) {
		ModuleBootstrapParams params = new ModuleBootstrapParams().setup(setup);
		RapidoidModules.get("OpenAPI").bootstrap(params);
	}

	public static void jpa(String... packages) {
		JPA.bootstrap(packages);
	}

	public static void all() {
		Setup setup = App.setup();

		jpa(App.path());
		auth(setup);
		oauth(setup);
		openapi(setup);

		adminCenter(setup);
	}

	private static String uri(String path) {
		return Msc.specialUri(path);
	}

}
