package org.rapidoid.http.handler;

/*
 * #%L
 * rapidoid-http-server
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Err;
import org.rapidoid.commons.MediaType;
import org.rapidoid.http.*;
import org.rapidoid.http.handler.lambda.*;
import org.rapidoid.http.handler.optimized.DelegatingFastParamsAwareReqHandler;
import org.rapidoid.http.handler.optimized.DelegatingFastParamsAwareReqRespHandler;
import org.rapidoid.http.handler.optimized.DelegatingFastParamsAwareRespHandler;
import org.rapidoid.http.handler.optimized.FastCallableHttpHandler;
import org.rapidoid.lambda.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpHandlers {

	public static FastHttpHandler from(FastHttp http, NParamLambda handler, MediaType contentType, HttpWrapper[] wrappers) {
		if (handler instanceof ReqHandler) {
			return new DelegatingFastParamsAwareReqHandler(http, contentType, wrappers, (ReqHandler) handler);

		} else if (handler instanceof ReqRespHandler) {
			return new DelegatingFastParamsAwareReqRespHandler(http, contentType, wrappers, (ReqRespHandler) handler);

		} else if (handler instanceof OneParamLambda) {

			OneParamLambda lambda = (OneParamLambda) handler;
			Method method = Cls.getLambdaMethod(lambda);
			Class<?> paramType = method.getParameterTypes()[0];

			if (paramType.equals(Req.class)) {
				return new DelegatingFastParamsAwareReqHandler(http, contentType, wrappers, lambda);
			} else if (paramType.equals(Resp.class)) {
				return new DelegatingFastParamsAwareRespHandler(http, contentType, wrappers, lambda);
			} else {
				return new OneParamLambdaHandler(http, contentType, wrappers, lambda);
			}

		} else if (handler instanceof TwoParamLambda) {

			TwoParamLambda lambda = (TwoParamLambda) handler;
			Method method = Cls.getLambdaMethod(lambda);
			Class<?> param1Type = method.getParameterTypes()[0];
			Class<?> param2Type = method.getParameterTypes()[1];

			if (param1Type.equals(Req.class) && param2Type.equals(Resp.class)) {
				return new DelegatingFastParamsAwareReqRespHandler(http, contentType, wrappers, lambda);
			} else {
				return new TwoParamLambdaHandler(http, contentType, wrappers, (TwoParamLambda) handler);
			}

		} else if (handler instanceof ThreeParamLambda) {
			return new ThreeParamLambdaHandler(http, contentType, wrappers, (ThreeParamLambda) handler);

		} else if (handler instanceof FourParamLambda) {
			return new FourParamLambdaHandler(http, contentType, wrappers, (FourParamLambda) handler);

		} else if (handler instanceof FiveParamLambda) {
			return new FiveParamLambdaHandler(http, contentType, wrappers, (FiveParamLambda) handler);

		} else if (handler instanceof SixParamLambda) {
			return new SixParamLambdaHandler(http, contentType, wrappers, (SixParamLambda) handler);

		} else if (handler instanceof SevenParamLambda) {
			return new SevenParamLambdaHandler(http, contentType, wrappers, (SevenParamLambda) handler);

		} else {
			throw Err.notExpected();
		}
	}

	public static void register(FastHttp[] httpImpls, String verb, String path, MediaType contentType, HttpWrapper[] wrappers, byte[] response) {
		for (FastHttp http : httpImpls) {
			http.on(verb, path, new FastStaticHttpHandler(http, contentType, response));
		}
	}

	@SuppressWarnings("unchecked")
	public static void register(FastHttp[] httpImpls, String verb, String path, MediaType contentType, HttpWrapper[] wrappers, Callable<?> handler) {
		for (FastHttp http : httpImpls) {
			http.on(verb, path, new FastCallableHttpHandler(http, contentType, wrappers, (Callable<Object>) handler));
		}
	}

	public static void register(FastHttp[] httpImpls, String verb, String path, MediaType contentType, HttpWrapper[] wrappers, NParamLambda lambda) {
		for (FastHttp http : httpImpls) {
			FastHttpHandler handler = HttpHandlers.from(http, lambda, contentType, wrappers);
			http.on(verb, path, handler);
		}
	}

	public static void register(FastHttp[] httpImpls, String verb, String path, MediaType contentType, HttpWrapper[] wrappers, Method method, Object instance) {
		for (FastHttp http : httpImpls) {
			http.on(verb, path, new MethodReqHandler(http, contentType, wrappers, method, instance));
		}
	}

}
