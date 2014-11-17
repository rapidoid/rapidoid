package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.util.Map;

public class AppClasses {

	final Class<?> main;
	final Map<String, Class<?>> services;
	final Map<String, Class<?>> pages;
	final Map<String, Class<?>> screens;

	public AppClasses(Class<?> main, Map<String, Class<?>> services, Map<String, Class<?>> pages,
			Map<String, Class<?>> screens) {
		this.main = main;
		this.services = services;
		this.pages = pages;
		this.screens = screens;
	}

	@Override
	public String toString() {
		return "AppClasses [main=" + main + ", services=" + services + ", pages=" + pages + ", screens=" + screens
				+ "]";
	}

}
