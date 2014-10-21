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
import org.rapidoid.pojo.PojoDispatchException;
import org.rapidoid.pojo.PojoDispatcher;
import org.rapidoid.pojo.PojoHandlerNotFoundException;
import org.rapidoid.pojo.PojoRequest;
import org.rapidoid.util.Prop;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;

public class PojoDispatcherImpl implements PojoDispatcher {

	private static final String[] EMPTY_STRING_ARRAY = {};

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
	public Object dispatch(PojoRequest request) throws PojoHandlerNotFoundException, PojoDispatchException {
		String[] parts = uriParts(request.path());
		int length = parts.length;

		Object res;

		if (length == 0) {
			res = process(request, "main", "index", parts, 0);
			if (res != null) {
				return res;
			} else {
				throw notFound();
			}
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

		throw notFound();
	}

	private Object process(PojoRequest request, String service, String action, String[] parts, int paramsFrom)
			throws PojoHandlerNotFoundException, PojoDispatchException {
		PojoServiceWrapper root = services.get(service);

		if (root != null) {
			Method method = root.getMethod(action);
			if (method != null) {
				return doDispatch(request, method, root.getTarget(), parts, paramsFrom);
			}
		}

		return null;
	}

	private Object doDispatch(PojoRequest request, Method method, Object service, String[] parts, int paramsFrom)
			throws PojoHandlerNotFoundException, PojoDispatchException {
		if (method != null) {

			Object[] args;
			try {
				int paramsSize = parts.length - paramsFrom;
				Class<?>[] types = method.getParameterTypes();
				args = new Object[types.length];

				int simpleParamIndex = 0;

				for (int i = 0; i < types.length; i++) {
					Class<?> type = types[i];
					TypeKind kind = U.kindOf(type);

					if (kind.isSimple()) {

						if (parts.length > paramsFrom + simpleParamIndex) {
							args[i] = U.convert(parts[paramsFrom + simpleParamIndex++], type);
						} else {
							throw error(null, "Not enough parameters!");
						}

					} else {
						args[i] = complexArg(i, type, request, parts, paramsFrom, paramsSize);
					}
				}
			} catch (Throwable e) {
				throw new PojoDispatchException("Cannot dispatch to POJO target!", e);
			}

			return U.invoke(method, service, args);

		} else {
			throw notFound();
		}
	}

	protected Object complexArg(int i, Class<?> type, PojoRequest request, String[] parts, int paramsFrom,
			int paramsSize) throws PojoDispatchException {

		if (type.equals(Map.class)) {
			return mapArg(request, parts, paramsFrom);
		} else if (type.equals(String[].class)) {
			return stringsArg(request, parts, paramsFrom, paramsSize);
		} else if (type.equals(List.class) || type.equals(Collection.class)) {
			return listArg(request, parts, paramsFrom, paramsSize);
		} else if (type.equals(Set.class)) {
			return setArg(request, parts, paramsFrom, paramsSize);
		} else if (isCustomType(type)) {
			return getCustomArg(request, type, parts, paramsFrom, paramsSize);
		} else if (type.getCanonicalName().startsWith("java")) {
			throw error(null, "Parameter type '%s' is not supported!", type.getCanonicalName());
		} else {
			return instantiateArg(request, type);
		}
	}

	private Object instantiateArg(PojoRequest request, Class<?> type) throws PojoDispatchException {
		try {
			Constructor<?> constructor = type.getConstructor();

			try {
				Object instance = constructor.newInstance();
				setBeanProperties(instance, request.params());
				return instance;

			} catch (Exception e) {
				throw error(e, "Cannot create a new instance of type: '%s'!", type.getCanonicalName());
			}

		} catch (NoSuchMethodException e) {
			throw error(e, "Cannot find a constructor with 0 parameters for type '%s'!", type.getCanonicalName());

		} catch (SecurityException e) {
			throw error(e, "Cannot retrieve the constructor with 0 parameters for type '%s'!", type.getCanonicalName());
		}

	}

	protected Object getCustomArg(PojoRequest request, Class<?> type, String[] parts, int paramsFrom, int paramsSize) {
		return null;
	}

	protected boolean isCustomType(Class<?> type) {
		return false;
	}

	private Set<?> setArg(PojoRequest request, String[] parts, int paramsFrom, int paramsSize) {
		if (parts.length > paramsFrom) {
			Set<String> arguments = U.set();

			for (int j = paramsFrom; j < parts.length; j++) {
				arguments.add(parts[j]);
			}
			return arguments;
		} else {
			return U.set();
		}
	}

	private List<?> listArg(PojoRequest request, String[] parts, int paramsFrom, int paramsSize) {
		if (parts.length > paramsFrom) {
			List<String> arguments = new ArrayList<String>(paramsSize);

			for (int j = paramsFrom; j < parts.length; j++) {
				arguments.add(parts[j]);
			}

			return arguments;
		} else {
			return U.list();
		}
	}

	private String[] stringsArg(PojoRequest request, String[] parts, int paramsFrom, int paramsSize) {
		if (parts.length > paramsFrom) {
			String[] arguments = new String[paramsSize];
			System.arraycopy(parts, paramsFrom, arguments, 0, paramsSize);
			return arguments;
		} else {
			return EMPTY_STRING_ARRAY;
		}
	}

	private Map<String, String> mapArg(PojoRequest request, String[] parts, int paramsFrom) {
		Map<String, String> params = request.params();

		for (int j = paramsFrom; j < parts.length; j++) {
			params.put("" + (j - paramsFrom + 1), parts[j]);
		}
		return params;
	}

	private static void setBeanProperties(Object instance, Map<String, String> paramsMap) {
		Map<String, Prop> props = U.propertiesOf(instance.getClass());

		for (Entry<String, String> entry : paramsMap.entrySet()) {
			Prop prop = props.get(entry.getKey());
			Object value = entry.getValue();
			prop.set(instance, value);
		}
	}

	private static PojoDispatchException error(Throwable cause, String msg, Object... args) {
		return new PojoDispatchException(U.format(msg, args), cause);
	}

	private static PojoHandlerNotFoundException notFound() {
		return new PojoHandlerNotFoundException();
	}

	private static String[] uriParts(String uri) {
		if (uri.isEmpty() || uri.equals("/")) {
			return EMPTY_STRING_ARRAY;
		}

		return uri.replaceAll("^/", "").replaceAll("/$", "").split("/");
	}

}
