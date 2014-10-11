package org.rapidoid.pojo;

/*
 * #%L
 * rapidoid-pojo
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rapidoid.util.U;

import com.rapidoid.http.Handler;
import com.rapidoid.http.HTTP;
import com.rapidoid.http.HttpExchange;

public class Pojo {

	protected static final String SUFFIX = "Service";

	public static void run(Object... controllers) {
		final POJODispatcher dispatcher = new POJODispatcher(controllers);

		HTTP.serve(new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				return dispatcher.dispatch(new WebReq(x)).asString();
			}
		});
	}

	public static void run(Class<?>... classes) {
		Object[] services = new Object[classes.length];
		for (int i = 0; i < services.length; i++) {
			try {
				services[i] = classes[i].newInstance();
			} catch (Exception e) {
				throw U.rte(e);
			}
		}
		Pojo.run(services);
	}

	public static void start() {

		List<Class<?>> classes = U.classpathClasses("*", ".+" + SUFFIX, null);

		Set<Class<?>> services = new HashSet<Class<?>>();
		for (Class<?> cls : classes) {
			services.add(cls);
		}

		run(services.toArray(new Class[services.size()]));
	}

}
