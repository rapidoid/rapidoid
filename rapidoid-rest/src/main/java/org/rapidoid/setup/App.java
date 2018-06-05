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

import org.rapidoid.RapidoidModule;
import org.rapidoid.RapidoidModules;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.RapidoidInitializer;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.data.JSON;
import org.rapidoid.env.Env;
import org.rapidoid.group.Groups;
import org.rapidoid.http.HttpRoutes;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.io.Res;
import org.rapidoid.ioc.Beans;
import org.rapidoid.ioc.IoC;
import org.rapidoid.ioc.IoCContext;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathScanner;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.scan.Scan;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscOpts;
import org.rapidoid.util.Once;
import org.rapidoid.web.Screen;

import java.util.List;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class App extends RapidoidInitializer {

	private static volatile String mainClassName;
	private static volatile String appPkgName;
	private static volatile String[] packages;
	private static volatile boolean dirty;
	private static volatile boolean restarted;

	private static volatile AppStatus status = AppStatus.NOT_STARTED;

	private static final Set<Class<?>> invoked = Coll.synchronizedSet();

	static volatile ClassLoader loader = App.class.getClassLoader();

	private static final Once boot = new Once();

	/**
	 * Initializes the app in atomic way.
	 * Won't serve requests until App.ready() is called.
	 */
	public static synchronized void init(String[] args, String... extraArgs) {
		AppStarter.startUp(args, extraArgs);

		status = AppStatus.INITIALIZING;

		// no implicit classpath scanning here
		boot();
	}

	/**
	 * Initializes the app in non-atomic way.
	 * Then starts serving requests immediately when routes are configured.
	 */
	public static synchronized void run(String[] args, String... extraArgs) {
		AppStarter.startUp(args, extraArgs);

		// no implicit classpath scanning here
		boot();

		// finish initialization and start the application
		onAppReady();

		boot();
	}

	/**
	 * Initializes the app in non-atomic way.
	 * Then scans the classpath for beans.
	 * Then starts serving requests immediately when routes are configured.
	 */
	public static synchronized void bootstrap(String[] args, String... extraArgs) {
		AppStarter.startUp(args, extraArgs);

		boot();

		App.scan(); // scan classpath for beans

		// finish initialization and start the application
		onAppReady();

		boot();
	}

	public synchronized static void boot() {
		if (boot.go()) {
			for (RapidoidModule module : RapidoidModules.getAll()) {
				module.boot();
			}
		}
	}

	public static synchronized void profiles(String... profiles) {
		Env.setProfiles(profiles);
		Conf.reset();
	}

	public static void path(String... packages) {
		App.packages = packages;
	}

	public static synchronized String[] path() {
		inferCallers();

		if (packages == null) {
			packages = appPkgName != null ? U.array(appPkgName) : new String[0];
		}

		return packages;
	}

	static void inferCallers() {
		if (!Msc.isPlatform() && !restarted && appPkgName == null && mainClassName == null) {

			appPkgName = Msc.getCallingPackage();

			if (mainClassName == null) {
				Class<?> mainClass = Msc.getCallingMainClass();
				invoked.add(mainClass);
				mainClassName = mainClass != null ? mainClass.getName() : null;
			}

			if (mainClassName != null || appPkgName != null) {
				Log.info("Inferred application root", "!main", mainClassName, "!package", appPkgName);
			}
		}
	}

	private static synchronized void restartApp() {
		if (!MscOpts.hasRapidoidWatch()) {
			Log.warn("Cannot reload/restart the application, module rapidoid-watch is missing!");
		}

		if (mainClassName == null) {
			Log.warn("Cannot reload/restart the application, the main app class couldn't be detected!");
		}

		Msc.logSection("!Restarting the web application...");

		restarted = true;

		notifyListenersBeforeRestart();

		resetAppStateBeforeRestart();

		if (MscOpts.hasRapidoidJPA()) {
			loader = ReloadUtil.reloader();
			ClasspathUtil.setDefaultClassLoader(loader);
		}

		reloadAndRunMainClass();

		restarted = true;

		notifyListenersAfterRestart();

		Log.info("!Successfully restarted the application!");

	}

	private static void notifyListenersAfterRestart() {
		Set<AppRestartListener> listeners = U.set(On.changes().getRestartListeners());

		for (AppRestartListener listener : listeners) {
			try {
				listener.afterAppRestart();
			} catch (Exception e) {
				Log.error("Error occurred in the app restart listener!", e);
			}
		}
	}

	private static void notifyListenersBeforeRestart() {
		Set<AppRestartListener> listeners = U.set(On.changes().getRestartListeners());

		for (AppRestartListener listener : listeners) {
			try {
				listener.beforeAppRestart();
			} catch (Exception e) {
				Log.error("Error occurred in the app restart listener!", e);
			}
		}
	}

	private static void reloadAndRunMainClass() {
		Class<?> entry;
		try {
			entry = loader.loadClass(mainClassName);
		} catch (ClassNotFoundException e) {
			Log.error("Cannot restart the application, the main class (app entry point) is missing!");
			return;
		}

		Msc.invokeMain(entry, U.arrayOf(String.class, Env.args()));
	}

	private static void resetAppStateBeforeRestart() {
		App.boot.reset();
		App.status = AppStatus.NOT_STARTED;
		App.dirty = false;
		App.packages = null;

		Groups.reset();
		Conf.reset();
		Env.reset();
		Res.reset();
		JSON.reset();
		Beany.reset();

		for (RapidoidModule mod : RapidoidModules.getAllAvailable()) {
			mod.restartApp();
		}

		AppStarter.reset();
		ClasspathScanner.reset();
		invoked.clear();

		Setups.reloadAll();

		Setups.initDefaults();
		Conf.reset(); // reset the config again
	}

	public static synchronized void resetGlobalState() {
		status = AppStatus.NOT_STARTED;
		mainClassName = null;
		appPkgName = null;
		restarted = false;
		dirty = false;
		packages = null;
		loader = App.class.getClassLoader();
		boot.reset();
		Setups.initDefaults();
		AppStarter.reset();
		invoked.clear();
	}

	static synchronized void notifyChanges() {
		if (!dirty) {
			dirty = true;
			Log.info("Detected class or resource changes");
		}
	}

	static boolean restartIfDirty() {
		if (dirty && mainClassName != null) {
			synchronized (Setups.class) {
				if (dirty && mainClassName != null) {
					restartApp();
					dirty = false;
					return true;
				}
			}
		}

		return false;
	}

	static synchronized List<Class<?>> findBeans(String... packages) {
		if (U.isEmpty(packages)) {
			packages = path();
		}

		return Scan.annotated(IoC.ANNOTATIONS).in(packages).loadAll();
	}

	public static synchronized boolean scan(String... packages) {
		String appPath = Conf.APP.entry("path").str().getOrNull();

		if (U.notEmpty(appPath)) {
			packages = appPath.split("\\s*,\\s*");
			App.path(packages);
		}

		List<Class<?>> beans = App.findBeans(packages);
		beans(beans.toArray());
		return !beans.isEmpty();
	}

	public static void beans(Object... beans) {
		setup().beans(beans);
	}

	public static IoCContext context() {
		return IoC.defaultContext();
	}

	static void filterAndInvokeMainClasses(Object[] beans) {
		Msc.filterAndInvokeMainClasses(beans, invoked);
	}

	static boolean isRestarted() {
		return restarted;
	}

	public static synchronized void register(Beans beans) {
		setup().register(beans);
	}

	public static synchronized void shutdown() {
		status = AppStatus.STOPPING;

		Setups.shutdownAll();

		status = AppStatus.STOPPED;
	}

	/**
	 * Completes the initialization and starts the application.
	 */
	public static synchronized void ready() {
		U.must(status == AppStatus.INITIALIZING, "App.init() must be called before App.ready()!");

		onAppReady();
	}

	private static void onAppReady() {
		status = AppStatus.RUNNING;
		IoC.ready();
		Setups.ready();
		Log.info("!Ready.");
	}

	public static AppStatus status() {
		return status;
	}

	public static Setup setup() {
		return Setups.main();
	}

	public static Screen gui() {
		return setup().gui();
	}

	public static Config config() {
		return On.setup().config();
	}

	public static Customization custom() {
		return On.setup().custom();
	}

	public static HttpRoutes routes() {
		return On.setup().routes();
	}

	public static RouteOptions defaults() {
		return On.setup().defaults();
	}
}