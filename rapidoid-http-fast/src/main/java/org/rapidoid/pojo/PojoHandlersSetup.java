package org.rapidoid.pojo;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.*;
import org.rapidoid.beany.Metadata;
import org.rapidoid.cls.Cls;
import org.rapidoid.http.ServerSetup;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.UTILS;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class PojoHandlersSetup implements Constants {

	private final ServerSetup server;
	private final List<Object> controllers;

	public PojoHandlersSetup(ServerSetup server, List<Object> controllers) {
		this.server = server;
		this.controllers = controllers;
	}

	public void register() {
		for (Object controller : controllers) {

			Class<? extends Object> clazz = controller.getClass();
			if (!clazz.getName().startsWith("org.rapidoid.log.")) { // FIXME clean-up

				Log.debug("Processing POJO", "class", clazz);
				List<String> componentPaths = getComponentNames(clazz);

				for (String ctxPath : componentPaths) {
					for (Method method : Cls.getMethods(clazz)) {
						if (shouldExpose(method)) {
							register(controller, ctxPath, method);
						}
					}
				}
			}
		}
	}

	private boolean shouldExpose(Method method) {
		boolean isUserDefined = !method.getDeclaringClass().equals(Object.class);

		int modifiers = method.getModifiers();

		boolean isAbstract = Modifier.isAbstract(modifiers);
		boolean isStatic = Modifier.isStatic(modifiers);
		boolean isPrivate = Modifier.isPrivate(modifiers);
		boolean isProtected = Modifier.isProtected(modifiers);

		return isUserDefined && !isAbstract && !isStatic && !isPrivate && !isProtected;
	}

	protected List<String> getComponentNames(Class<?> component) {
		Controller controller = Metadata.classAnnotation(component, Controller.class);

		if (controller != null) {
			return U.list(controller.value());
		} else {
			return U.list("/");
		}
	}

	private void register(Object controller, String ctxPath, Method method) {
		Log.info("Registering POJO handler", "controller", controller, "ctxPath", ctxPath, "method", method);

		for (Annotation ann : method.getAnnotations()) {

			if (ann instanceof GET) {
				GET get = (GET) ann;
				server.get(pathOf(method, ctxPath, get.value())).json(method, controller);

			} else if (ann instanceof POST) {
				POST post = (POST) ann;
				server.post(pathOf(method, ctxPath, post.value())).json(method, controller);

			} else if (ann instanceof PUT) {
				PUT put = (PUT) ann;
				server.put(pathOf(method, ctxPath, put.value())).json(method, controller);

			} else if (ann instanceof DELETE) {
				DELETE delete = (DELETE) ann;
				server.delete(pathOf(method, ctxPath, delete.value())).json(method, controller);

			} else if (ann instanceof Page) {
				Page page = (Page) ann;
				server.page(pathOf(method, ctxPath, page.value())).gui(method, controller);
			}
		}
	}

	private String pathOf(Method method, String ctxPath, String uri) {
		String path = !U.isEmpty(uri) ? uri : method.getName();
		return UTILS.uri(ctxPath, path);
	}

}
