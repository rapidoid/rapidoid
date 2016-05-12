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
import org.rapidoid.annotation.*;
import org.rapidoid.commons.Coll;
import org.rapidoid.config.Conf;
import org.rapidoid.data.JSON;
import org.rapidoid.io.Res;
import org.rapidoid.ioc.IoC;
import org.rapidoid.ioc.IoCContext;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.lambda.NParamLambda;
import org.rapidoid.log.Log;
import org.rapidoid.reload.Reload;
import org.rapidoid.render.Templates;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.scan.Scan;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class App extends RapidoidThing {

	private static final Class<?>[] ANNOTATIONS = {Controller.class, Service.class, Run.class, Named.class, Singleton.class};

	private static volatile String[] path;

	private static volatile String mainClassName;
	private static volatile String appPkgName;
	private static volatile boolean dirty;
	private static volatile boolean restarted;

	private static final Set<Class<?>> invoked = Coll.synchronizedSet();

	static volatile ClassLoader loader;

	private static Map<List<String>, List<Class<?>>> beansCache = Coll.autoExpandingMap(new Mapper<List<String>, List<Class<?>>>() {
		@SuppressWarnings("unchecked")
		@Override
		public List<Class<?>> map(List<String> packages) throws Exception {
			String[] pkgs = packages.toArray(new String[packages.size()]);
			return Scan.annotated((Class<? extends Annotation>[]) ANNOTATIONS).in(pkgs).loadAll();
		}
	});

	static {
		resetGlobalState();
	}

	public static void args(String... args) {
		Conf.args(args);
	}

	public static void path(String... path) {
		App.path = path;
	}

	public static synchronized String[] path() {
		inferCallers();

		if (U.isEmpty(App.path)) {
			App.path = new String[]{appPkgName};
		}

		return path;
	}

	static void inferCallers() {
		if (!restarted && appPkgName == null && mainClassName == null) {
			String pkg = Msc.getCallingPackage();

			appPkgName = pkg;

			if (mainClassName == null) {
				Class<?> mainClass = Msc.getCallingMainClass();
				invoked.add(mainClass);
				mainClassName = mainClass != null ? mainClass.getName() : null;
			}

			if (mainClassName != null || pkg != null) {
				Log.info("Inferring application root", "!main", mainClassName, "!package", pkg);
			}
		}
	}

	private static void restartApp() {
		U.notNull(mainClassName, "Cannot restart, the main class is unknown!");

		Msc.logSection("!Restarting the web application...");

		restarted = true;
		App.path = null;

		Conf.reload();
		Res.reset();
		Templates.reset();
		JSON.reset();
		AppBootstrap.reset();

		for (Setup setup : Setup.instances()) {
			setup.reload();
		}

		Setup.initDefaults();

		loader = Reload.createClassLoader();
		ClasspathUtil.setDefaultClassLoader(loader);

		Class<?> entry;
		try {
			entry = loader.loadClass(mainClassName);
		} catch (ClassNotFoundException e) {
			Log.error("Cannot restart the application, the main class (app entry point) is missing!");
			return;
		}

		Msc.invokeMain(entry, Conf.getArgs());

		Log.info("!Successfully restarted the application!");
	}

	public static void resetGlobalState() {
		mainClassName = null;
		appPkgName = null;
		restarted = false;
		dirty = false;
		path = null;
		loader = Setup.class.getClassLoader();
		Setup.initDefaults();
		AppBootstrap.reset();
		beansCache.clear();
		invoked.clear();
	}

	public static void notifyChanges() {
		if (!dirty) {
			dirty = true;
			Log.info("Detected class or resource changes");
		}
	}

	static void restartIfDirty() {
		if (dirty) {
			synchronized (Setup.class) {
				if (dirty) {
					restartApp();
					dirty = false;
				}
			}
		}
	}

	public static List<Class<?>> findBeans(String... packages) {
		if (U.isEmpty(packages)) {
			packages = path();
		}

		return beansCache.get(U.list(packages));
	}

	public static void scan(String... packages) {
		beans(App.findBeans(packages).toArray());
	}

	public static void beans(Object... beans) {
		for (Object bean : beans) {
			U.notNull(bean, "bean");

			if (bean instanceof NParamLambda) {
				throw U.rte("Expected a bean, but found lambda: " + bean);
			}
		}

		filterAndInvokeMainClasses(beans);

		PojoHandlersSetup.from(Setup.ON, beans).register();
	}

	public static IoCContext context() {
		return IoC.defaultContext();
	}

	public static AppBootstrap bootstrap(String... args) {
		args(args);
		scan();
		return new AppBootstrap();
	}

	static void filterAndInvokeMainClasses(Object[] beans) {
		Msc.filterAndInvokeMainClasses(beans, invoked);
	}

}
