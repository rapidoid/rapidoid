package org.rapidoid.pages;

/*
 * #%L
 * rapidoid-pages
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

import java.util.List;

import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.inject.IoC;
import org.rapidoid.util.U;

public class PageHandler implements Handler {

	private static final String PAGE_PREFIX = "_page_";

	@Override
	public Object handle(HttpExchange x) throws Exception {

		String pageName = Pages.pageName(x);

		if (pageName != null) {

			List<Class<?>> pageClasses = U.classpathClassesBySuffix("Page", null, null);
			Page page = x.session(PAGE_PREFIX + pageName, null);

			if (page == null) {
				Class<?> pageClass = findPageClass(pageClasses, pageName);

				U.must(PageComponent.class.isAssignableFrom(pageClass), "The class %s must implement WebPage!",
						pageClass);

				if (pageClass != null) {
					page = (Page) U.newInstance(pageClass);
					IoC.autowire(page);
					x.setSession(PAGE_PREFIX + pageName, page);
				} else {
					return x.notFound();
				}
			}

			x.html();
			page.render(x);
			return x;

		} else {
			return null;
		}
	}

	private Class<?> findPageClass(List<Class<?>> pageClasses, String pageName) {
		for (Class<?> cls : pageClasses) {
			if (cls.getSimpleName().equals(pageName + "Page")) {
				return cls;
			}
		}

		return null;
	}

}
