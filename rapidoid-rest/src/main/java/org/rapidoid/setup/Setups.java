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

import org.essentials4j.Do;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.RapidoidInitializer;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.data.JSON;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.impl.HttpRoutesImpl;
import org.rapidoid.ioc.IoC;
import org.rapidoid.ioc.IoCContext;
import org.rapidoid.job.Jobs;
import org.rapidoid.u.U;
import org.rapidoid.util.LazyInit;
import org.rapidoid.web.Screen;
import org.rapidoid.web.ScreenBean;

import java.util.Collections;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class Setups extends RapidoidInitializer {

	private static final LazyInit<DefaultSetup> DEFAULT = new LazyInit<>(DefaultSetup::new);

	private static final List<Setup> instances = Coll.synchronizedList();

	static {
		init();
	}

	private static void init() {
		if (Ctxs.getPersisterProvider() == null) {
			Ctxs.setPersisterProvider(new CustomizableSetupAwarePersisterProvider());
		}

		JSON.warmUp();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			shutdownAll();
			Jobs.shutdownNow();
		}));
	}

	public static Setup create(String name) {
		IoCContext ioc = IoC.createContext().name(name);
		Config config = Conf.section(name);

		Customization customization = new Customization(name, My.custom(), config);
		HttpRoutesImpl routes = new HttpRoutesImpl(name, customization);
		Screen gui = new ScreenBean();
		FastHttp http = new FastHttp(routes, config, gui);

		Setup setup = new SetupImpl(name, "main", http, ioc, config, customization, routes, gui, false);

		instances.add(setup);
		return setup;
	}

	public static Setup main() {
		return DEFAULT.get().main;
	}

	public static synchronized void haltAll() {
		instances().forEach(Setup::halt);
	}

	public static synchronized void shutdownAll() {
		instances().forEach(Setup::shutdown);
	}

	public static synchronized boolean isAnyRunning() {
		return Do.findIn(instances()).exists(Setup::isRunning);
	}

	static List<Setup> instances() {
		return Collections.unmodifiableList(instances);
	}

	static void register(Setup setup) {
		instances.add(setup);
	}

	static void deregister(Setup setup) {
		instances.remove(setup);
	}

	static void initDefaults() {
		DefaultSetup defaultSetup = DEFAULT.getValue();

		if (defaultSetup != null) {
			defaultSetup.initDefaults();
		}
	}

	public static void ready() {
		instances().forEach(Setup::activate);
	}

	static boolean isAppSetupAtomic() {
		return App.status() == AppStatus.INITIALIZING;
	}

	public static void clear() {
		synchronized (Setups.class) {
			for (Setup setup : Setups.instances()) {
				setup.routes().reset();
				U.must(setup.routes().all().isEmpty());
			}

			Setups.instances.clear();
		}
	}

	static void reloadAll() {
		synchronized (Setups.class) {
			for (Setup setup : Setups.instances()) {
				setup.reload();
			}
		}
	}

}
