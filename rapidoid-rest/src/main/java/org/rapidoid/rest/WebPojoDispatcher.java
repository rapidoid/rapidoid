package org.rapidoid.rest;

/*
 * #%L
 * rapidoid-rest
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
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.DELETE;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.annotation.PUT;
import org.rapidoid.annotation.RESTful;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.aop.AOP;
import org.rapidoid.arr.Arr;
import org.rapidoid.beany.Metadata;
import org.rapidoid.cls.Cls;
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

	public WebPojoDispatcher(Map<String, Class<?>> services) {
		super(services);
	}

	public WebPojoDispatcher(Class<?>... classes) {
		this(Cls.classMap(U.list(classes)));
	}

	@Override
	protected boolean isCustomType(Class<?> type) {
		return type.equals(HttpExchange.class) || super.isCustomType(type);
	}

	@Override
	protected Object getCustomArg(PojoRequest request, Class<?> type, String[] parts, int paramsFrom, int paramsSize) {
		if (type.equals(HttpExchange.class)) {
			U.must(request instanceof WebReq);
			WebReq webReq = (WebReq) request;
			return webReq.getExchange();
		} else {
			return super.getCustomArg(request, type, parts, paramsFrom, paramsSize);
		}
	}

	@Override
	protected List<String> getServiceNames(Class<?> service) {
		RESTful restful = Metadata.classAnnotation(service, RESTful.class);

		if (restful != null) {
			return U.list(restful.value());
		} else {
			return super.getServiceNames(service);
		}
	}

	@Override
	public Object dispatch(PojoRequest req) throws PojoHandlerNotFoundException, PojoDispatchException {
		try {
			return super.dispatch(req);
		} catch (PojoHandlerNotFoundException e) {
			return alternativeDispatch(req);
		}
	}

	private Object alternativeDispatch(PojoRequest req) throws PojoHandlerNotFoundException, PojoDispatchException {
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
	protected List<DispatchReq> getMethodActions(String servicePath, Method method) {
		List<DispatchReq> reqs = U.list();

		for (Annotation ann : method.getAnnotations()) {
			DispatchReq req = req(servicePath, ann, method);
			if (req != null) {
				reqs.add(req);
			}
		}

		return reqs;
	}

	private DispatchReq req(String servicePath, Annotation ann, Method method) {
		String url;

		if (ann instanceof GET) {
			url = ((GET) ann).value();
		} else if (ann instanceof POST) {
			url = ((POST) ann).value();
		} else if (ann instanceof PUT) {
			url = ((PUT) ann).value();
		} else if (ann instanceof DELETE) {
			url = ((DELETE) ann).value();
		} else {
			return null;
		}

		String name = reqName(method, url);
		String path = UTILS.path(servicePath, name);
		String verb = ann.annotationType().getSimpleName();

		return new DispatchReq(verb, path);
	}

	private String reqName(Method method, String url) {
		return U.isEmpty(url) ? method.getName() : url;
	}

	@Override
	protected void preprocess(PojoRequest req, Method method, Object service, Object[] args) {
		HttpExchange x = ((WebReq) req).getExchange();
		x.setTransactionMode(TransactionMode.READ_ONLY);
	}

	@Override
	protected Object invoke(PojoRequest req, Method method, Object service, Object[] args) {
		HttpExchange x = ((WebReq) req).getExchange();
		return AOP.invoke(x, method, service, args);
	}

}
