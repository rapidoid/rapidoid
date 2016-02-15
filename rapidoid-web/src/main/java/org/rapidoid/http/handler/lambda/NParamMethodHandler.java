package org.rapidoid.http.handler.lambda;

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

import org.rapidoid.cls.Cls;
import org.rapidoid.commons.MediaType;
import org.rapidoid.http.FastHttp;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.Req;
import org.rapidoid.http.handler.FastParamsAwareHttpHandler;
import org.rapidoid.http.handler.param.ParamRetriever;
import org.rapidoid.http.handler.param.ParamRetrievers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class NParamMethodHandler extends FastParamsAwareHttpHandler {

	private final ParamRetriever[] paramRetrievers;

	protected final Method method;

	private final String paramsAsStr;

	public NParamMethodHandler(FastHttp http, MediaType contentType, HttpWrapper[] wrappers, Method method) {
		super(http, contentType, wrappers);
		this.method = method;

		Class<?>[] paramTypes = method.getParameterTypes();
		String[] paramNames = Cls.getMethodParameterNames(method);
		Annotation[][] annotations = method.getParameterAnnotations();

		this.paramRetrievers = new ParamRetriever[paramTypes.length];

		String par = "";
		for (int i = 0; i < paramRetrievers.length; i++) {
			paramRetrievers[i] = ParamRetrievers.createParamRetriever(paramTypes[i], paramNames[i], annotations[i]);
			if (i > 0) {
				par += ", ";
			}
			par += paramTypes[i].getSimpleName() + " " + paramNames[i];
		}
		paramsAsStr = "(" + par + ")";
	}

	protected Object arg(Req req, int index) {
		return paramRetrievers[index].getParamValue(req);
	}

	protected Object[] args(Req req) {
		Object[] args = new Object[paramRetrievers.length];

		for (int i = 0; i < args.length; i++) {
			args[i] = arg(req, i);
		}

		return args;
	}

	protected String paramsToString() {
		return paramsAsStr;
	}

}
