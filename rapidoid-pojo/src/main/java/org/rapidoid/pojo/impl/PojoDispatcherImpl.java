package org.rapidoid.pojo.impl;

/*
 * #%L
 * rapidoid-pojo
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rapidoid.beany.BeanProperties;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.pojo.POJO;
import org.rapidoid.pojo.PojoDispatchException;
import org.rapidoid.pojo.PojoDispatcher;
import org.rapidoid.pojo.PojoHandlerNotFoundException;
import org.rapidoid.pojo.PojoRequest;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Constants;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

public class PojoDispatcherImpl implements PojoDispatcher, Constants {

	private static final Map<Class<?>, PojoServiceWrapper> WRAPPERS = UTILS
			.autoExpandingMap(new Mapper<Class<?>, PojoServiceWrapper>() {
				@Override
				public PojoServiceWrapper map(Class<?> serviceClass) throws Exception {
					return new PojoServiceWrapper(serviceClass);
				}
			});

	private final Map<String, Class<?>> services;

	public PojoDispatcherImpl(Map<String, Class<?>> services) {
		this.services = services;
	}

	@SuppressWarnings("unused")
	private static String nameOf(Class<?> serviceClass) {
		return U.mid(serviceClass.getSimpleName(), 0, -POJO.SERVICE_SUFFIX.length());
	}

	@Override
	public Object dispatch(PojoRequest request) throws PojoHandlerNotFoundException, PojoDispatchException {
		String[] parts = uriParts(request.path());
		int length = parts.length;

		if (length == 0) {
			return process(request, "main", "index", parts, 0);
		}

		if (length >= 1) {
			try {
				return process(request, parts[0], "index", parts, 1);
			} catch (PojoHandlerNotFoundException e) {
				// ignore, continue trying...
			}

			try {
				return process(request, "main", parts[0], parts, 1);
			} catch (PojoHandlerNotFoundException e) {
				// ignore, continue trying...
			}
		}

		if (length >= 2) {
			return process(request, parts[0], parts[1], parts, 2);
		}

		throw notFound();
	}

	private Object process(PojoRequest request, String service, String action, String[] parts, int paramsFrom)
			throws PojoHandlerNotFoundException, PojoDispatchException {

		PojoServiceWrapper wrapper = wrapper(service);

		if (wrapper != null) {
			Method method = wrapper.getMethod(action);
			if (method != null) {
				Object serviceInstance = Cls.newInstance(wrapper.getTarget());
				return doDispatch(request, method, serviceInstance, parts, paramsFrom);
			}
		}

		throw notFound();
	}

	private PojoServiceWrapper wrapper(String service) {

		String name = U.capitalized(service) + "Service";

		Class<?> serviceClass = services.get(name);
		if (serviceClass == null) {
			return null;
		}

		return WRAPPERS.get(serviceClass);
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
					TypeKind kind = Cls.kindOf(type);

					if (kind.isSimple()) {

						if (parts.length > paramsFrom + simpleParamIndex) {
							args[i] = Cls.convert(parts[paramsFrom + simpleParamIndex++], type);
						} else {
							throw error(null, "Not enough parameters!");
						}

					} else if (type.equals(Object.class)) {
						Class<?> defaultType = getDefaultType(service);

						if (defaultType != null) {
							args[i] = instantiateArg(request, defaultType);
						} else {
							throw error(null, "Cannot provide value for parameter of type Object!");
						}
					} else {
						args[i] = complexArg(i, type, request, parts, paramsFrom, paramsSize);
					}
				}
			} catch (Throwable e) {
				throw new PojoDispatchException("Cannot dispatch to POJO target!", e);
			}

			Object result = Cls.invoke(method, service, args);
			if (result == null && method.getReturnType().equals(void.class)) {
				result = "OK";
			}
			return result;

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

	private Class<?> getDefaultType(Object service) {
		Object clazz = Beany.getPropValue(service, "clazz");
		return (Class<?>) (clazz instanceof Class ? clazz : null);
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
		BeanProperties props = Beany.propertiesOf(instance.getClass());

		for (Entry<String, String> entry : paramsMap.entrySet()) {
			Prop prop = props.get(entry.getKey());
			Object value = entry.getValue();
			prop.set(instance, value);
		}
	}

	private static PojoDispatchException error(Throwable cause, String msg, Object... args) {
		return new PojoDispatchException(U.readable(msg, args), cause);
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
