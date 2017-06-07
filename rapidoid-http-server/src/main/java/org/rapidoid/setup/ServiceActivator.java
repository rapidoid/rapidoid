package org.rapidoid.setup;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.config.ConfigOption;
import org.rapidoid.config.ConfigOptions;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.2.3")
public class ServiceActivator extends RapidoidThing {

	private static final Set<String> checked = U.set();

	static void activateServices() {
		bootstrapServices(On.setup(), "app", Conf.APP);
		bootstrapServices(Admin.setup(), "admin", Conf.ADMIN);

		verifyCoverage();
	}

	private static void verifyCoverage() {
		for (ConfigOption service : ConfigOptions.SERVICES) {
			if (!checked.contains(service.getName())) {
				throw U.rte("Service activation not supported: " + service.getName());
			}
		}
	}

	private static void bootstrapServices(Setup setup, String setupName, Config config) {
		if (boot(setupName, config, "overview")) goodies().overview(setup);
		if (boot(setupName, config, "application")) goodies().application(setup);
		if (boot(setupName, config, "lifecycle")) goodies().lifecycle(setup);
		if (boot(setupName, config, "processes")) goodies().processes(setup);
		if (boot(setupName, config, "manageables")) goodies().manageables(setup);
		if (boot(setupName, config, "jmx")) goodies().jmx(setup);
		if (boot(setupName, config, "metrics")) goodies().metrics(setup);
		if (boot(setupName, config, "deploy")) goodies().deploy(setup);
		if (boot(setupName, config, "ping")) goodies().ping(setup);
		if (boot(setupName, config, "auth")) goodies().auth(setup);
		if (boot(setupName, config, "oauth")) goodies().oauth(setup);
		if (boot(setupName, config, "entities")) goodies().entities(setup);
		if (boot(setupName, config, "center")) goodies().adminCenter(setup);
		if (boot(setupName, config, "welcome")) goodies().welcome(setup);
		if (boot(setupName, config, "status")) goodies().status(setup);
		if (boot(setupName, config, "discovery")) goodies().discovery(setup);
		if (boot(setupName, config, "echo")) goodies().echo(setup);
	}

	private static boolean boot(String setupName, Config config, String service) {

		if (!documented(service)) throw U.rte("Service not documented: " + service);

		checked.add(service);

		return Msc.bootService(config, service);
	}

	private static boolean documented(String service) {
		for (ConfigOption opt : ConfigOptions.SERVICES) {
			if (service.equals(opt.getName())) {
				return true;
			}
		}
		return false;
	}

	private static IGoodies goodies() {
		return AppBootstrap.getGoodies();
	}

}
