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

import java.util.Map;

import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tags;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.inject.IoC;
import org.rapidoid.util.U;

public class PageHandler implements Handler {

	private final Map<String, Class<?>> pages;

	public PageHandler(Map<String, Class<?>> pages) {
		this.pages = pages;
	}

	@Override
	public Object handle(HttpExchange x) throws Exception {

		String pageName = Pages.pageName(x);
		if (pageName == null) {
			return x.notFound();
		}

		String pageClassName = U.capitalized(pageName) + "Page";

		Class<?> pageClass = pages.get(pageClassName);
		if (pageClass == null) {
			return x.notFound();
		}

		x.setSession(Pages.SESSION_PAGE_NAME, pageClassName);

		Object page = U.newInstance(pageClass);
		IoC.autowire(page);

		TagContext ctx = Tags.context();
		x.setSession(Pages.SESSION_CTX, ctx);

		return Pages.render(x, page);
	}

}
