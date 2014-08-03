package org.rapidoid.web;

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
import java.util.Set;

import org.rapidoid.util.U;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.rapidoid.http.Handler;
import com.rapidoid.http.Web;
import com.rapidoid.http.WebExchange;

public class Pojo {

	protected static final String SUFFIX = "Service";

	public static void run(int port, Object... controllers) {
		final POJODispatcher dispatcher = new POJODispatcher(controllers);
		
		Web.handle(new Handler() {
			@Override
			public void handle(WebExchange x) {
				PojowebResponse resp = dispatcher.dispatch(new WebReq(x));
				x.write(resp.asString());
				x.done();
			}
		});
		
		Web.start();
	}

	public static void run(String[] args, Object... services) {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
		Pojo.run(port, services);
	}

	public static void run(String[] args, Class<?>... classes) {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
		Object[] services = new Object[classes.length];
		for (int i = 0; i < services.length; i++) {
			try {
				services[i] = classes[i].newInstance();
			} catch (Exception e) {
				throw U.rte(e);
			}
		}
		Pojo.run(port, services);
	}

	public static void main(String[] args) {
		Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage(""))
				.setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner()));

		Set<Class<? extends Object>> classes = reflections.getSubTypesOf(Object.class);

		Set<Class<?>> services = new HashSet<Class<?>>();
		for (Class<? extends Object> clazz : classes) {
			if (clazz.getSimpleName().endsWith(SUFFIX)) {
				services.add(clazz);
			}
		}

		run(args, services.toArray(new Class[services.size()]));
	}

}
