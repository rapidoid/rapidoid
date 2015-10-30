package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.util.Collection;
import java.util.Map;

import org.rapidoid.annotation.App;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.Web;
import org.rapidoid.beany.Metadata;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class AppClasses {

	public final Class<?> main;
	public final Map<String, Class<?>> components;

	public AppClasses(Class<?> main, Map<String, Class<?>> components) {
		this.main = main;
		this.components = components;
	}

	public static AppClasses from(Class<?>... classes) {
		Class<?> main = null;
		Map<String, Class<?>> services = U.map();

		for (Class<?> cls : classes) {
			String name = cls.getSimpleName();
			if (Metadata.isAnnotated(cls, App.class)) {
				main = cls;
			} else if (Metadata.isAnnotated(cls, Web.class)) {
				services.put(name, cls);
			}
		}

		return new AppClasses(main, services);
	}

	public static AppClasses from(Collection<Class<?>> classes) {
		Class<?>[] classesArr = new Class<?>[classes.size()];
		classes.toArray(classesArr);
		return from(classesArr);
	}

	@Override
	public String toString() {
		return "AppClasses [main=" + main + ", components=" + components + "]";
	}

}
