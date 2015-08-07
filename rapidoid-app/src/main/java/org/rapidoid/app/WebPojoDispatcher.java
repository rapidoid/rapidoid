package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Cookie;
import org.rapidoid.annotation.DELETE;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.Header;
import org.rapidoid.annotation.POST;
import org.rapidoid.annotation.PUT;
import org.rapidoid.annotation.View;
import org.rapidoid.annotation.Web;
import org.rapidoid.annotation.Since;
import org.rapidoid.aop.AOP;
import org.rapidoid.arr.Arr;
import org.rapidoid.beany.Metadata;
import org.rapidoid.cls.Cls;
import org.rapidoid.dispatch.DispatchResult;
import org.rapidoid.dispatch.PojoDispatchException;
import org.rapidoid.dispatch.PojoHandlerNotFoundException;
import org.rapidoid.dispatch.PojoRequest;
import org.rapidoid.dispatch.impl.DispatchReq;
import org.rapidoid.dispatch.impl.PojoDispatcherImpl;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class WebPojoDispatcher extends PojoDispatcherImpl {

	public WebPojoDispatcher(Map<String, Class<?>> components) {
		super(components);
	}

	public WebPojoDispatcher(Class<?>... classes) {
		this(Cls.classMap(U.list(classes)));
	}

	@Override
	protected boolean isCustomType(Class<?> type) {
		return type.equals(HttpExchange.class) || type.equals(byte[].class) || type.equals(byte[][].class)
				|| super.isCustomType(type);
	}

	@Override
	protected Object getCustomArg(PojoRequest request, Class<?> type, String[] parts, int paramsFrom, int paramsSize) {
		if (type.equals(HttpExchange.class)) {
			return exchange(request);

		} else if (type.equals(byte[].class)) {
			HttpExchange x = exchange(request);
			U.must(x.files().size() == 1, "Expected exactly 1 file uploaded for the byte[] parameter!");
			return U.single(x.files().values());

		} else if (type.equals(byte[][].class)) {
			HttpExchange x = exchange(request);
			byte[][] files = new byte[x.files().size()][];

			int ind = 0;
			for (byte[] file : x.files().values()) {
				files[ind++] = file;
			}

			return files;

		} else {
			return super.getCustomArg(request, type, parts, paramsFrom, paramsSize);
		}
	}

	private HttpExchange exchange(PojoRequest request) {
		U.must(request instanceof WebReq);
		WebReq webReq = (WebReq) request;
		return webReq.getExchange();
	}

	@Override
	protected List<String> getComponentNames(Class<?> component) {
		Web web = Metadata.classAnnotation(component, Web.class);

		if (web != null) {
			return U.list(web.value());
		} else {
			return super.getComponentNames(component);
		}
	}

	@Override
	public DispatchResult dispatch(PojoRequest req) throws PojoHandlerNotFoundException, PojoDispatchException {
		try {
			return super.dispatch(req);
		} catch (PojoHandlerNotFoundException e) {
			return alternativeDispatch(req);
		}
	}

	private DispatchResult alternativeDispatch(PojoRequest req) throws PojoHandlerNotFoundException,
			PojoDispatchException {
		String[] parts = uriParts(req.path());

		for (int i = 0; i < parts.length; i++) {
			try {
				String path = U.join("/", Arr.subarray(parts, 0, i));
				return process(req, req.command(), path, parts, i + 1);
			} catch (PojoHandlerNotFoundException e) {
				// ignore, continue trying...
			}
		}

		throw notFound();
	}

	private static String[] uriParts(String uri) {
		if (uri.isEmpty() || uri.equals("/")) {
			return EMPTY_STRING_ARRAY;
		}

		return uri.replaceAll("^/", "").replaceAll("/$", "").split("/");
	}

	@Override
	protected List<DispatchReq> getMethodActions(String componentPath, Method method) {
		List<DispatchReq> reqs = U.list();

		for (Annotation ann : method.getAnnotations()) {
			List<DispatchReq> req = req(componentPath, ann, method);
			if (req != null) {
				reqs.addAll(req);
			}
		}

		return reqs;
	}

	private List<DispatchReq> req(String componentPath, Annotation ann, Method method) {
		String url;

		if (ann instanceof GET) {
			url = ((GET) ann).value();
		} else if (ann instanceof POST) {
			url = ((POST) ann).value();
		} else if (ann instanceof PUT) {
			url = ((PUT) ann).value();
		} else if (ann instanceof DELETE) {
			url = ((DELETE) ann).value();
		} else if (ann instanceof View) {
			url = ((View) ann).value();
		} else {
			return null;
		}

		String name = reqName(method, url);
		String path = UTILS.path(componentPath, name);

		if (!(ann instanceof View)) {
			String verb = ann.annotationType().getSimpleName();
			return U.list(new DispatchReq(verb, path, true));
		} else {
			return U.list(new DispatchReq("GET", path, false), new DispatchReq("POST", path, false));
		}
	}

	private String reqName(Method method, String url) {
		return U.isEmpty(url) ? method.getName() : url;
	}

	@Override
	protected void preprocess(PojoRequest req, Method method, Object component, Object[] args) {}

	@Override
	protected Object invoke(PojoRequest req, Method method, Object component, Object[] args) {
		HttpExchange x = ((WebReq) req).getExchange();
		return AOP.invoke(x, method, component, args);
	}

	@Override
	protected Object customSimpleArg(PojoRequest request, Annotation[] annotations) {

		Cookie cookie = Metadata.get(annotations, Cookie.class);
		if (cookie != null) {
			HttpExchange x = exchange(request);
			return x.cookie(cookie.value(), null);
		}

		Header header = Metadata.get(annotations, Header.class);
		if (header != null) {
			HttpExchange x = exchange(request);
			return x.header(header.value(), null);
		}

		return null;
	}

	@Override
	protected boolean isCustomSimpleArg(PojoRequest request, Annotation[] annotations) {
		return Metadata.get(annotations, Cookie.class) != null || Metadata.get(annotations, Header.class) != null;
	}

}
