package org.rapidoid.setup;

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
		if (boot(setupName, "overview")) goodies().overview(setup);
		if (boot(setupName, "application")) goodies().application(setup);
		if (boot(setupName, "lifecycle")) goodies().lifecycle(setup);
		if (boot(setupName, "jmx")) goodies().jmx(setup);
		if (boot(setupName, "metrics")) goodies().metrics(setup);
		if (boot(setupName, "deploy")) goodies().deploy(setup);
		if (boot(setupName, "ping")) goodies().ping(setup);
		if (boot(setupName, "auth")) goodies().auth(setup);
		if (boot(setupName, "oauth")) goodies().oauth(setup);
		if (boot(setupName, "entities")) goodies().entities(setup);
		if (boot(setupName, "center")) goodies().adminCenter(setup);
		if (boot(setupName, "welcome")) goodies().welcome(setup);
		if (boot(setupName, "status")) goodies().status(setup);
	}

	private static boolean boot(String setupName, String service) {

		if (!documented(service)) throw U.rte("Service not documented: " + service);

		checked.add(service);

		return Msc.bootService(setupName, service);
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
