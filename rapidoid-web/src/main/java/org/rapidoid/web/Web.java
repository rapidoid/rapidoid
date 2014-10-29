package org.rapidoid.web;

/*
 * #%L
 * rapidoid-web
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

import org.rapidoid.pojo.POJO;
import org.rapidoid.pojo.PojoDispatchException;
import org.rapidoid.pojo.PojoDispatcher;
import org.rapidoid.pojo.PojoHandlerNotFoundException;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

import com.rapidoid.http.HTTP;
import com.rapidoid.http.Handler;
import com.rapidoid.http.HttpExchange;

public class Web {

	public static void run(Object... services) {
		serve(new WebPojoDispatcher(services));
	}

	public static void run(Class<?>... classes) {
		serve(new WebPojoDispatcher(UTILS.instantiateAll(classes)));
	}

	private static void serve(final PojoDispatcher dispatcher) {
		HTTP.serve(new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				try {
					return dispatcher.dispatch(new WebReq(x));
				} catch (PojoHandlerNotFoundException e) {
					return x.response(404, "Handler not found!", e);
				} catch (PojoDispatchException e) {
					return x.response(500, "Cannot initialize handler argument(s)!", e);
				}
			}
		});
	}

	public static void start() {
		List<Class<?>> services = POJO.scanServices();
		run(services.toArray(new Class[services.size()]));
	}

	public static void main(String[] args) {
		U.args(args);
		start();
	}

}
