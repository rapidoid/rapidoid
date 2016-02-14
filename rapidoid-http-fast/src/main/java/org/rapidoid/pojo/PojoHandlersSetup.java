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
import org.rapidoid.wire.Wire;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class PojoHandlersSetup implements Constants {

	private final ServerSetup server;
	private final Object[] controllers;

	private PojoHandlersSetup(ServerSetup server, Object[] controllers) {
		this.server = server;
		this.controllers = controllers;
	}

	public static PojoHandlersSetup from(ServerSetup server, Object[] controllers) {
		return new PojoHandlersSetup(server, controllers);
	}

	public void register() {
		process(true);
	}

	public void deregister() {
		process(false);
	}

	private void process(boolean register) {
		for (Object controller : controllers) {

			U.notNull(controller, "controller");

			Class<?> clazz = (controller instanceof Class<?>) ? (Class<?>) controller : controller.getClass();

			if (!Cls.isBeanType(clazz)) {
				throw new RuntimeException("Expected a controller instance, but found value of type: " + clazz.getName());
			}

			if (controller instanceof Class<?>) {
				controller = register ? Wire.singleton(clazz) : null;
			}

			if (!clazz.getName().startsWith("org.rapidoid.log.")) { // FIXME clean-up

				Log.debug("Processing POJO", "class", clazz);

				List<String> componentPaths = getComponentNames(clazz);

				for (String ctxPath : componentPaths) {
					for (Method method : Cls.getMethods(clazz)) {
						if (shouldExpose(method)) {
							registerOrDeregister(register, controller, ctxPath, method);
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

		return isUserDefined && !isAbstract && !isStatic && !isPrivate && !isProtected && method.getAnnotations().length > 0;
	}

	protected List<String> getComponentNames(Class<?> component) {
		Controller controller = Metadata.classAnnotation(component, Controller.class);

		if (controller != null) {
			return U.list(controller.value());
		} else {
			return U.list("/");
		}
	}

	private void registerOrDeregister(boolean register, Object controller, String ctxPath, Method method) {
		for (Annotation ann : method.getAnnotations()) {

			if (ann instanceof GET) {
				GET get = (GET) ann;
				String path = pathOf(method, ctxPath, get.value());

				if (register) {
					server.get(path).json(method, controller);
				} else {
					server.deregister(GET, path);
				}

			} else if (ann instanceof POST) {
				POST post = (POST) ann;
				String path = pathOf(method, ctxPath, post.value());

				if (register) {
					server.post(path).json(method, controller);
				} else {
					server.deregister(POST, path);
				}

			} else if (ann instanceof PUT) {
				PUT put = (PUT) ann;
				String path = pathOf(method, ctxPath, put.value());

				if (register) {
					server.put(path).json(method, controller);
				} else {
					server.deregister(PUT, path);
				}

			} else if (ann instanceof DELETE) {
				DELETE delete = (DELETE) ann;
				String path = pathOf(method, ctxPath, delete.value());

				if (register) {
					server.delete(path).json(method, controller);
				} else {
					server.deregister(DELETE, path);
				}

			} else if (ann instanceof PATCH) {
				PATCH patch = (PATCH) ann;
				String path = pathOf(method, ctxPath, patch.value());

				if (register) {
					server.patch(path).json(method, controller);
				} else {
					server.deregister(PATCH, path);
				}

			} else if (ann instanceof OPTIONS) {
				OPTIONS options = (OPTIONS) ann;
				String path = pathOf(method, ctxPath, options.value());

				if (register) {
					server.options(path).json(method, controller);
				} else {
					server.deregister(OPTIONS, path);
				}

			} else if (ann instanceof HEAD) {
				HEAD head = (HEAD) ann;
				String path = pathOf(method, ctxPath, head.value());

				if (register) {
					server.head(path).json(method, controller);
				} else {
					server.deregister(HEAD, path);
				}

			} else if (ann instanceof TRACE) {
				TRACE trace = (TRACE) ann;
				String path = pathOf(method, ctxPath, trace.value());

				if (register) {
					server.trace(path).json(method, controller);
				} else {
					server.deregister(TRACE, path);
				}

			} else if (ann instanceof Page) {
				Page page = (Page) ann;
				String path = pathOf(method, ctxPath, page.value());

				if (register) {
					server.page(path).gui(method, controller);
				} else {
					server.deregister(GET, path);
					server.deregister(POST, path);
				}
			}
		}
	}

	private String pathOf(Method method, String ctxPath, String uri) {
		String path = !U.isEmpty(uri) ? uri : method.getName();
		return UTILS.uri(ctxPath, path);
	}

}
