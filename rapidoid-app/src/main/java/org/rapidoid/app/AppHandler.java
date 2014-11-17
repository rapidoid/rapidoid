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

import java.util.List;

import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.pages.Pages;
import org.rapidoid.pojo.PojoDispatchException;
import org.rapidoid.pojo.PojoHandlerNotFoundException;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;
import org.rapidoid.web.WebPojoDispatcher;
import org.rapidoid.web.WebReq;

public class AppHandler implements Handler {

	private final WebPojoDispatcher dispatcher;

	private Object main;

	private Object[] screens;

	public AppHandler(AppStructure app) {
		main = U.newInstance(app.main);

		List<Class<?>> services = app.services;
		this.dispatcher = new WebPojoDispatcher(Cls.instantiateAll(services));

		screens = Cls.instantiateAll(app.screens);
	}

	@Override
	public Object handle(HttpExchange x) throws Exception {

		String path = x.path();

		Object screen = getScreen(path);
		if (screen == null) {
			return x.notFound();
		}

		AppPage appPage = new AppPage(main, screens, screen);

		try {
			return dispatcher.dispatch(new WebReq(x));
		} catch (PojoHandlerNotFoundException e) {
			return Pages.render(x, appPage);

		} catch (PojoDispatchException e) {
			return x.response(500, "Cannot initialize handler argument(s)!", e);
		}
	}

	private Object getScreen(String path) {
		for (Object screen : screens) {
			if (Apps.screenUrl(screen).equals(path)) {
				return screen;
			}
		}
		return null;
	}

}
