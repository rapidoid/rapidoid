package org.rapidoid.pojo.impl;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rapidoid.pojo.POJO;
import org.rapidoid.pojo.PojoDispatcher;
import org.rapidoid.pojo.PojoRequest;
import org.rapidoid.pojo.PojoResponse;
import org.rapidoid.util.Prop;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;

public class PojoDispatcherImpl implements PojoDispatcher {

	private static final String[] EMPTY_STRING_ARRAY = new String[] {};

	private final Map<String, PojoServiceWrapper> services = new HashMap<String, PojoServiceWrapper>();

	public PojoDispatcherImpl(Object... services) {
		for (Object service : services) {
			String name = service.getClass().getSimpleName();
			U.must(name.endsWith(POJO.SERVICE_SUFFIX), "The service class doesn't have a '%s' suffix: %s",
					POJO.SERVICE_SUFFIX, name);

			U.info("Initializing service: " + name);
			name = name.substring(0, name.length() - POJO.SERVICE_SUFFIX.length()).toLowerCase();

			PojoServiceWrapper pojoService = new PojoServiceWrapper(service);
			pojoService.init();
			this.services.put(name, pojoService);
		}
	}

	@Override
	public PojoResponse dispatch(PojoRequest request) {
		String[] parts = request.pathParts();
		int length = parts.length;

		PojoResponse res;

		if (length == 0) {
			res = process(request, "main", "index", parts, 0);
			return res != null ? res : notFound(request);
		}

		if (length >= 1) {
			res = process(request, parts[0], "index", parts, 1);
			if (res != null) {
				return res;
			}

			res = process(request, "main", parts[0], parts, 1);
			if (res != null) {
				return res;
			}
		}

		if (length >= 2) {
			res = process(request, parts[0], parts[1], parts, 2);
			if (res != null) {
				return res;
			}
		}

		return notFound(request);
	}

	private PojoResponse process(PojoRequest request, String service, String action, String[] parts, int paramsFrom) {
		PojoServiceWrapper root = services.get(service);

		if (root != null) {
			Method method = root.getMethod(action);
			if (method != null) {
				return doDispatch(request, method, root.getTarget(), parts, paramsFrom);
			}
		}

		return null;
	}

	private PojoResponse doDispatch(PojoRequest request, Method method, Object service, String[] parts, int paramsFrom) {
		int paramsSize = parts.length - paramsFrom;

		if (method != null) {

			Class<?>[] types = method.getParameterTypes();
			Object[] args = new Object[types.length];

			int simpleParamIndex = 0;

			for (int i = 0; i < types.length; i++) {
				Class<?> type = types[i];
				TypeKind kind = U.kindOf(type);

				if (kind.isSimple()) {

					if (parts.length > paramsFrom + simpleParamIndex) {
						args[i] = U.convert(parts[paramsFrom + simpleParamIndex++], type);
					} else {
						return error("Not enough parameters!");
					}

				} else if (type.equals(Map.class)) {

					Map<String, Object> params = request.paramsMap();

					for (int j = paramsFrom; j < parts.length; j++) {
						params.put("" + (j - paramsFrom + 1), parts[j]);
					}

					args[i] = params;

				} else if (type.equals(String[].class)) {

					if (parts.length > paramsFrom) {
						String[] arguments = new String[paramsSize];
						System.arraycopy(parts, paramsFrom, arguments, 0, paramsSize);
						args[i] = arguments;
					} else {
						args[i] = EMPTY_STRING_ARRAY;
					}

				} else if (type.equals(List.class) || type.equals(Collection.class)) {

					if (parts.length > paramsFrom) {
						List<String> arguments = new ArrayList<String>(paramsSize);

						for (int j = paramsFrom; j < parts.length; j++) {
							arguments.add(parts[j]);
						}

						args[i] = arguments;
					} else {
						args[i] = U.list();
					}

				} else if (type.equals(Set.class)) {

					if (parts.length > paramsFrom) {
						Set<String> arguments = U.set();

						for (int j = paramsFrom; j < parts.length; j++) {
							arguments.add(parts[j]);
						}
						args[i] = arguments;
					} else {
						args[i] = U.set();
					}

				} else if (type.getCanonicalName().startsWith("java")) {
					return error("Parameter type '%s' is not supported!", type.getCanonicalName());
				} else {
					try {
						Constructor<?> constructor = type.getConstructor();
						try {
							Object instance = constructor.newInstance();
							setBeanProperties(instance, request.paramsMap());
							args[i] = instance;
						} catch (Exception e) {
							e.printStackTrace();
							return error("Cannot create a new instance of type: '%s'!", type.getCanonicalName());
						}

					} catch (NoSuchMethodException e) {
						return error("Cannot find a constructor with 0 parameters for type '%s'!",
								type.getCanonicalName());
					} catch (SecurityException e) {
						return error("Cannot retrieve the constructor with 0 parameters for type '%s'!",
								type.getCanonicalName());
					}

				}
			}

			Object result = U.invoke(method, service, args);

			return new PojoResponseImpl(result, false);

		} else {
			return notFound(request);
		}
	}

	private void setBeanProperties(Object instance, Map<String, Object> paramsMap) {
		Map<String, Prop> props = U.propertiesOf(instance.getClass());

		for (Entry<String, Object> entry : paramsMap.entrySet()) {
			Prop prop = props.get(entry.getKey());
			Object value = entry.getValue();
			prop.set(instance, value);
		}
	}

	private PojoResponse error(String msg, Object... args) {
		return new PojoResponseImpl("ERROR: " + String.format(msg, args), true);
	}

	private PojoResponseImpl notFound(PojoRequest request) {
		return new PojoResponseImpl("Not found: " + request.path(), true);
	}

}
