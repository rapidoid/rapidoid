/*-
 * #%L
 * rapidoid-rest
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

package org.rapidoid.setup;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.RapidoidInitializer;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.impl.HttpRoutesImpl;
import org.rapidoid.ioc.IoC;
import org.rapidoid.security.Role;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.web.Screen;
import org.rapidoid.web.ScreenBean;

@Authors("Nikolche Mihajlovski")
@Since("5.3.2")
public class DefaultSetup extends RapidoidInitializer {

	private static final String MAIN_ZONE = Msc.isPlatform() ? "platform" : "main";
	private static final String ADMIN_ZONE = Msc.isPlatform() ? "platform" : "admin";

	private static final Config MAIN_CFG = Msc.isPlatform() ? Conf.RAPIDOID : Conf.ON;
	private static final Config ADMIN_CFG = Msc.isPlatform() ? Conf.RAPIDOID_ADMIN : Conf.ADMIN;

	final Setup main;
	final Setup admin;

	DefaultSetup() {
		boolean onSameServer = appAndAdminOnSameServer();

		Customization appCustomization = new Customization("main", My.custom(), Conf.ROOT);
		Customization adminCustomization = onSameServer ? appCustomization : new Customization("admin", My.custom(), Conf.ROOT);

		HttpRoutesImpl appRoutes = new HttpRoutesImpl("main", appCustomization);
		HttpRoutesImpl adminRoutes = onSameServer ? appRoutes : new HttpRoutesImpl("admin", adminCustomization);

		Screen gui = new ScreenBean();

		main = new Setup("main", MAIN_ZONE, IoC.defaultContext(), MAIN_CFG, appCustomization, appRoutes, gui);
		admin = new Setup("admin", ADMIN_ZONE, IoC.defaultContext(), ADMIN_CFG, adminCustomization, adminRoutes, gui);

		Setups.register(main);
		Setups.register(admin);

		initDefaults();
	}

	void initDefaults() {
		admin.defaults().roles(Role.ADMINISTRATOR);
	}

	static boolean appAndAdminOnSameServer() {
		String mainPort = MAIN_CFG.entry("port").str().getOrNull();
		String adminPort = ADMIN_CFG.entry("port").str().getOrNull();

		return U.eq(mainPort, adminPort);
	}

}
