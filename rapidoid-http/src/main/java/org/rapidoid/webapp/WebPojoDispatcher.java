package org.rapidoid.webapp;

/*
 * #%L
 * rapidoid-http
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
import org.rapidoid.annotation.Page;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.Web;
import org.rapidoid.aop.AOP;
import org.rapidoid.arr.Arr;
import org.rapidoid.beany.Metadata;
import org.rapidoid.ctx.Classes;
import org.rapidoid.dispatch.DispatchResult;
import org.rapidoid.dispatch.PojoDispatchException;
import org.rapidoid.dispatch.PojoHandlerNotFoundException;
import org.rapidoid.dispatch.PojoRequest;
import org.rapidoid.dispatch.impl.DispatchReq;
import org.rapidoid.dispatch.impl.DispatchReqKind;
import org.rapidoid.dispatch.impl.PojoDispatcherImpl;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class WebPojoDispatcher extends PojoDispatcherImpl {

	public WebPojoDispatcher(Classes components) {
		super(components);
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
			return U.list(web.url());
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
		Map<String, Object> config = U.synchronizedMap();

		if (ann instanceof GET) {
			url = ((GET) ann).url();

		} else if (ann instanceof POST) {
			url = ((POST) ann).url();

		} else if (ann instanceof PUT) {
			url = ((PUT) ann).url();

		} else if (ann instanceof DELETE) {
			url = ((DELETE) ann).url();

		} else if (ann instanceof Page) {
			Page page = (Page) ann;
			url = page.url();

			config.put("raw", page.raw());
			config.put("navbar", page.navbar());
			config.put("search", page.search());
			config.put("profile", page.profile());
			config.put("login", page.login());

			if (!page.title().isEmpty()) {
				config.put("title", page.title());
			}

		} else {
			return null;
		}

		String name = reqName(method, url);
		String path = UTILS.path(componentPath, name);

		if (ann instanceof Page) {
			return U.list(new DispatchReq("GET", path, DispatchReqKind.PAGE, config), new DispatchReq("POST", path,
					DispatchReqKind.PAGE, config));
		} else {
			String verb = ann.annotationType().getSimpleName().toUpperCase();
			return U.list(new DispatchReq(verb, path, DispatchReqKind.SERVICE, config));
		}
	}

	private String reqName(Method method, String url) {
		return U.isEmpty(url) ? method.getName() : url;
	}

	@Override
	protected void preprocess(PojoRequest req, Method method, Object component, Object[] args) {}

	@Override
	protected Object invoke(PojoRequest req, Method method, Object component, Object[] args) {
		return AOP.invoke(null, method, component, args);
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
