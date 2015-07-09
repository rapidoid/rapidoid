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
import org.rapidoid.annotation.Page;
import org.rapidoid.annotation.RESTful;
import org.rapidoid.annotation.Screen;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Metadata;
import org.rapidoid.dispatch.PojoDispatcher;
import org.rapidoid.rest.WebPojoDispatcher;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class AppClasses {

	public final Class<?> main;
	public final Map<String, Class<?>> services;
	public final Map<String, Class<?>> pages;
	public final Map<String, Class<?>> screens;
	public final PojoDispatcher dispatcher;

	public AppClasses(Class<?> main, Map<String, Class<?>> services, Map<String, Class<?>> pages,
			Map<String, Class<?>> screens) {
		this.main = main;
		this.services = services;
		this.pages = pages;
		this.screens = screens;
		this.dispatcher = new WebPojoDispatcher(services);
	}

	public static AppClasses from(Class<?>... classes) {
		Class<?> main = null;
		Map<String, Class<?>> services = U.map();
		Map<String, Class<?>> pages = U.map();
		Map<String, Class<?>> screens = U.map();

		for (Class<?> cls : classes) {
			String name = cls.getSimpleName();
			if (Metadata.isAnnotated(cls, App.class)) {
				main = cls;
			} else if (Metadata.isAnnotated(cls, RESTful.class)) {
				services.put(name, cls);
			} else if (Metadata.isAnnotated(cls, Screen.class)) {
				screens.put(name, cls);
			} else if (Metadata.isAnnotated(cls, Page.class)) {
				pages.put(name, cls);
			}
		}

		return new AppClasses(main, services, pages, screens);
	}

	public static AppClasses from(Collection<Class<?>> classes) {
		Class<?>[] classesArr = new Class<?>[classes.size()];
		classes.toArray(classesArr);
		return from(classesArr);
	}

	@Override
	public String toString() {
		return "AppClasses [main=" + main + ", services=" + services + ", pages=" + pages + ", screens=" + screens
				+ ", dispatcher=" + dispatcher + "]";
	}

}
