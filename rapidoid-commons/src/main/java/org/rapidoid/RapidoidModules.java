package org.rapidoid;

/*
 * #%L
 * rapidoid-commons
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.RapidoidModuleDesc;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.u.U;

import java.util.*;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class RapidoidModules extends RapidoidThing {

	private static final Comparator<RapidoidModule> MODULE_COMPARATOR = new Comparator<RapidoidModule>() {
		@Override
		public int compare(RapidoidModule mod1, RapidoidModule mod2) {

			RapidoidModuleDesc desc1 = mod1.getClass().getAnnotation(RapidoidModuleDesc.class);
			RapidoidModuleDesc desc2 = mod2.getClass().getAnnotation(RapidoidModuleDesc.class);

			return desc1.order() - desc2.order();
		}
	};

	public static List<RapidoidModule> getAll() {
		return all(false);
	}

	public static List<RapidoidModule> getAllAvailable() {
		return all(true);
	}

	public static List<RapidoidModule> all(boolean availableOnly) {
		List<RapidoidModule> modules = U.list();

		addServiceLoaderModules(availableOnly, modules);

		addBuiltInModules(modules);

		validate(modules);

		Collections.sort(modules, MODULE_COMPARATOR);

		return modules;
	}

	private static void addBuiltInModules(List<RapidoidModule> modules) {
		for (String clsName : Cls.getRapidoidClasses()) {
			if (clsName.endsWith("Module")) {
				Class<?> cls = Cls.getClassIfExists(clsName);

				if (cls != null && RapidoidModule.class.isAssignableFrom(cls) && !cls.isInterface()) {
					modules.add((RapidoidModule) Cls.newInstance(cls));
				}
			}
		}
	}

	private static void addServiceLoaderModules(boolean availableOnly, List<RapidoidModule> modules) {
		Iterator<RapidoidModule> it = ServiceLoader.load(RapidoidModule.class).iterator();

		while (it.hasNext()) {
			RapidoidModule mod;

			if (availableOnly) {
				try {
					mod = it.next();
				} catch (ServiceConfigurationError e) {
					mod = null;
					// ignore it
				}
			} else {
				mod = it.next();
			}

			if (mod != null) {
				modules.add(mod);
			}
		}
	}

	private static void validate(List<RapidoidModule> modules) {
		for (RapidoidModule module : modules) {
			RapidoidModuleDesc desc = module.getClass().getAnnotation(RapidoidModuleDesc.class);
			U.must(desc != null, "The module must be annotated with %s!", RapidoidModuleDesc.class);
		}
	}

}
