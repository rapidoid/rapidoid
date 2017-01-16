package org.rapidoid.http.handler;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Err;
import org.rapidoid.http.*;
import org.rapidoid.http.handler.lambda.*;
import org.rapidoid.http.handler.optimized.CallableHttpHandler;
import org.rapidoid.http.handler.optimized.DelegatingParamsAwareReqHandler;
import org.rapidoid.http.handler.optimized.DelegatingParamsAwareReqRespHandler;
import org.rapidoid.http.handler.optimized.DelegatingParamsAwareRespHandler;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.lambda.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/*
 * #%L
 * rapidoid-http-server
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpHandlers extends RapidoidThing {

	public static HttpHandler from(FastHttp http, HttpRoutes routes, NParamLambda handler, RouteOptions options) {

		if (handler instanceof ReqHandler) {
			return new DelegatingParamsAwareReqHandler(http, routes, options, (ReqHandler) handler);

		} else if (handler instanceof ReqRespHandler) {
			return new DelegatingParamsAwareReqRespHandler(http, routes, options, (ReqRespHandler) handler);

		} else if (handler instanceof OneParamLambda) {

			OneParamLambda lambda = (OneParamLambda) handler;
			Method method = Cls.getLambdaMethod(lambda);
			Class<?> paramType = method.getParameterTypes()[0];

			if (paramType.equals(Req.class)) {
				return new DelegatingParamsAwareReqHandler(http, routes, options, lambda);
			} else if (paramType.equals(Resp.class)) {
				return new DelegatingParamsAwareRespHandler(http, routes, options, lambda);
			} else {
				return new OneParamLambdaHandler(http, routes, options, lambda);
			}

		} else if (handler instanceof TwoParamLambda) {

			TwoParamLambda lambda = (TwoParamLambda) handler;
			Method method = Cls.getLambdaMethod(lambda);
			Class<?> param1Type = method.getParameterTypes()[0];
			Class<?> param2Type = method.getParameterTypes()[1];

			if (param1Type.equals(Req.class) && param2Type.equals(Resp.class)) {
				return new DelegatingParamsAwareReqRespHandler(http, routes, options, lambda);
			} else {
				return new TwoParamLambdaHandler(http, routes, options, (TwoParamLambda) handler);
			}

		} else if (handler instanceof ThreeParamLambda) {
			return new ThreeParamLambdaHandler(http, routes, options, (ThreeParamLambda) handler);

		} else if (handler instanceof FourParamLambda) {
			return new FourParamLambdaHandler(http, routes, options, (FourParamLambda) handler);

		} else if (handler instanceof FiveParamLambda) {
			return new FiveParamLambdaHandler(http, routes, options, (FiveParamLambda) handler);

		} else if (handler instanceof SixParamLambda) {
			return new SixParamLambdaHandler(http, routes, options, (SixParamLambda) handler);

		} else if (handler instanceof SevenParamLambda) {
			return new SevenParamLambdaHandler(http, routes, options, (SevenParamLambda) handler);

		} else {
			throw Err.notExpected();
		}
	}

	public static void registerStatic(FastHttp http, HttpRoutes routes, String verb, String path, RouteOptions options, byte[] response) {
		routes.on(verb, path, new StaticHttpHandler(options, response));
	}

	public static void registerPredefined(FastHttp http, HttpRoutes routes, String verb, String path, RouteOptions options, Object response) {
		routes.on(verb, path, new PredefinedResponseHandler(http, routes, options, response));
	}

	public static void register(FastHttp http, HttpRoutes routes, String verb, String path, RouteOptions options, Callable<?> handler) {
		routes.on(verb, path, new CallableHttpHandler(http, routes, options, handler));
	}

	public static void register(FastHttp http, HttpRoutes routes, String verb, String path, RouteOptions options, NParamLambda lambda) {
		HttpHandler handler = HttpHandlers.from(http, routes, lambda, options);
		routes.on(verb, path, handler);
	}

	public static void register(FastHttp http, HttpRoutes routes, String verb, String path, RouteOptions options, Method method, Object instance) {
		routes.on(verb, path, new MethodReqHandler(http, routes, options, method, instance));
	}

}
