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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Param;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.BeanProperties;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Metadata;
import org.rapidoid.beany.Prop;
import org.rapidoid.log.Log;
import org.rapidoid.pojo.PojoDispatchException;
import org.rapidoid.pojo.PojoDispatcher;
import org.rapidoid.pojo.PojoHandlerNotFoundException;
import org.rapidoid.pojo.PojoRequest;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Constants;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class PojoDispatcherImpl implements PojoDispatcher, Constants {

	private static final String[] NO_PARTS = {};

	protected final Map<DispatchReq, DispatchTarget> mappings = U.map();

	public PojoDispatcherImpl(Map<String, Class<?>> services) {
		init(services);
	}

	private void init(Map<String, Class<?>> services) {
		for (Class<?> service : services.values()) {
			List<String> servicePaths = getServiceNames(service);
			for (String servicePath : servicePaths) {

				for (Method method : service.getMethods()) {
					if (shouldExpose(method)) {
						List<DispatchReq> actions = getMethodActions(servicePath, method);

						for (DispatchReq action : actions) {
							mappings.put(action, new DispatchTarget(service, method));
							Log.info("Registered web handler", "request", action, "method", method);
						}
					}
				}

			}
		}
	}

	protected List<String> getServiceNames(Class<?> service) {
		return U.list(service.getSimpleName());
	}

	@Override
	public Object dispatch(PojoRequest req) throws PojoHandlerNotFoundException, PojoDispatchException {
		return process(req, req.command(), req.path(), NO_PARTS, 0);
	}

	protected Object process(PojoRequest req, String command, String path, String[] parts, int paramsFrom)
			throws PojoHandlerNotFoundException, PojoDispatchException {

		// normalize the path
		path = UTILS.path(path);

		DispatchTarget target = mappings.get(new DispatchReq(command, path));

		if (target != null) {
			Object serviceInstance = Cls.newInstance(target.clazz);
			if (target.method != null) {
				return doDispatch(req, target.method, serviceInstance, parts, paramsFrom);
			} else {
				throw notFound();
			}
		}

		throw notFound();
	}

	private Object doDispatch(PojoRequest request, Method method, Object service, String[] parts, int paramsFrom)
			throws PojoHandlerNotFoundException, PojoDispatchException {

		Object[] args = setupArgs(request, method, service, parts, paramsFrom);

		preprocess(request, method, service, args);

		Object result = Cls.invoke(method, service, args);

		result = postprocess(request, method, service, args, result);

		return result;
	}

	private Object[] setupArgs(PojoRequest request, Method method, Object service, String[] parts, int paramsFrom)
			throws PojoDispatchException {

		Object[] args;

		try {
			int paramsSize = parts.length - paramsFrom;
			Class<?>[] types = method.getParameterTypes();
			Annotation[][] annotations = method.getParameterAnnotations();
			args = new Object[types.length];

			int subUrlParamIndex = 0;

			for (int i = 0; i < types.length; i++) {
				Class<?> type = types[i];
				TypeKind kind = Cls.kindOf(type);

				if (kind.isSimple()) {

					Param param = Metadata.get(annotations[i], Param.class);

					if (param != null) {
						String paramName = param.value();
						String val = request.params().get(paramName);
						args[i] = Cls.convert(val, type);
					} else {
						if (parts.length > paramsFrom + subUrlParamIndex) {
							args[i] = Cls.convert(parts[paramsFrom + subUrlParamIndex++], type);
						} else {
							throw error(null, "Not enough parameters!");
						}
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

		return args;
	}

	protected void preprocess(PojoRequest request, Method method, Object service, Object[] args) {}

	protected Object postprocess(PojoRequest request, Method method, Object service, Object[] args, Object result) {
		if (result == null && method.getReturnType().equals(void.class)) {
			result = "OK";
		}
		return result;
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

	protected List<DispatchReq> getMethodActions(String servicePath, Method method) {
		String path = UTILS.path(servicePath, method.getName());
		return U.list(new DispatchReq("", path));
	}

	private boolean shouldExpose(Method method) {
		boolean isUserDefined = !method.getDeclaringClass().equals(Object.class);

		int modifiers = method.getModifiers();
		boolean isPublic = !Modifier.isAbstract(modifiers) && Modifier.isPublic(modifiers);

		return isUserDefined && isPublic;
	}

	private static void setBeanProperties(Object instance, Map<String, String> paramsMap) {
		BeanProperties props = Beany.propertiesOf(instance.getClass());

		for (Entry<String, String> entry : paramsMap.entrySet()) {
			Prop prop = props.get(entry.getKey());
			if (prop != null) {
				Object value = entry.getValue();
				prop.set(instance, value);
			}
		}
	}

	protected static PojoDispatchException error(Throwable cause, String msg, Object... args) {
		return new PojoDispatchException(U.nice(msg, args), cause);
	}

	protected static PojoHandlerNotFoundException notFound() {
		return new PojoHandlerNotFoundException();
	}

}
