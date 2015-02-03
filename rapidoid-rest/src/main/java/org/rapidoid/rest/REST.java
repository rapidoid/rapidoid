package org.rapidoid.rest;

/*
 * #%L
 * rapidoid-rest
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

import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.pojo.POJO;
import org.rapidoid.pojo.PojoDispatchException;
import org.rapidoid.pojo.PojoDispatcher;
import org.rapidoid.pojo.PojoHandlerNotFoundException;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class REST {

	public static void run(Class<?>... classes) {
		serve(new WebPojoDispatcher(classes));
	}

	private static void serve(final PojoDispatcher dispatcher) {
		HTTP.serve(new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				try {
					return dispatcher.dispatch(new WebReq(x));
				} catch (PojoHandlerNotFoundException e) {
					throw x.notFound();
				} catch (PojoDispatchException e) {
					return x.errorResponse(e);
				}
			}
		});
	}

	public static void start() {
		List<Class<?>> services = POJO.scanServices();
		run(services.toArray(new Class[services.size()]));
	}

	public static void main(String[] args) {
		Conf.args(args);
		start();
	}

}
