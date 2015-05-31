package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
import org.rapidoid.annotation.Since;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpProtocol;
import org.rapidoid.pages.Pages;
import org.rapidoid.rest.WebPojoDispatcher;
import org.rapidoid.util.CustomizableClassLoader;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class AppHandler implements Handler {

	private static final String DISPATCHER = "dispatcher";

	private CustomizableClassLoader classLoader;

	public AppHandler() {
		this(null);
	}

	public AppHandler(CustomizableClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public Object handle(final HttpExchange x) throws Exception {
		x.setClassLoader(classLoader);
		Object result = processReq(x);
		HttpProtocol.processResponse(x, result);
		return x;
	}

	static Object processReq(HttpExchange x) {

		final AppClasses appCls = Apps.getAppClasses(x, x.getClassLoader());

		WebPojoDispatcher dispatcher = (WebPojoDispatcher) appCls.ctx.get(DISPATCHER);

		if (dispatcher == null) {
			dispatcher = new WebPojoDispatcher(appCls.services);
			appCls.ctx.put(DISPATCHER, dispatcher);
		}

		Object result = Pages.dispatch(x, dispatcher, appCls.pages);

		if (result != null) {
			return result;
		}

		Object view = new AppPageGeneric(x, appCls);

		if (Pages.isEmiting(x)) {
			return Pages.emit(x, view);
		} else {
			return Pages.serve(x, view);
		}
	}

}
