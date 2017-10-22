package org.rapidoid.setup;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.env.Env;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.impl.HttpRoutesImpl;
import org.rapidoid.ioc.IoC;
import org.rapidoid.security.Role;
import org.rapidoid.util.Msc;

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
@Since("5.3.2")
public class DefaultSetup extends RapidoidInitializer {

	private static final String MAIN_ZONE = Msc.isMultiAppPlatform() ? "platform" : "main";
	private static final String ADMIN_ZONE = Msc.isMultiAppPlatform() ? "platform" : "admin";

	final Setup on;
	final Setup admin;

	DefaultSetup() {
		boolean onSameServer = Setup.appAndAdminOnSameServer();

		Customization appCustomization = new Customization("main", My.custom(), Conf.ROOT);
		Customization adminCustomization = onSameServer ? appCustomization : new Customization("admin", My.custom(), Conf.ROOT);

		HttpRoutesImpl appRoutes = new HttpRoutesImpl("main", appCustomization);
		HttpRoutesImpl adminRoutes = onSameServer ? appRoutes : new HttpRoutesImpl("admin", adminCustomization);

		on = new Setup("main", MAIN_ZONE, IoC.defaultContext(), Setup.MAIN_CFG, appCustomization, appRoutes);
		admin = new Setup("admin", ADMIN_ZONE, IoC.defaultContext(), Setup.ADMIN_CFG, adminCustomization, adminRoutes);

		Setup.instances.add(on);
		Setup.instances.add(admin);

		initDefaults();
	}

	void initDefaults() {
		admin.defaults().roles(Role.ADMINISTRATOR);

		admin.onInit(new Runnable() {
			@Override
			public void run() {
				if (Env.dev()) {
//					AuthBootstrap.bootstrapAdminCredentials();
				}
			}
		});
	}

}
