package org.rapidoid.aop;

/*
 * #%L
 * rapidoid-aop
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Arr;
import org.rapidoid.lambda.Lmbd;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class AOP {

	private static volatile InterceptorConfig[] INTERCEPTORS = {};

	public static void reset() {
		INTERCEPTORS = new InterceptorConfig[0];
	}

	public static synchronized void intercept(AOPInterceptor interceptor, Class<? extends Annotation>... annotated) {
		InterceptorConfig config = new InterceptorConfig(interceptor, annotated);
		INTERCEPTORS = Arr.expand(INTERCEPTORS, config);
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(Object context, Method m, Object target, Object... args) {
		Annotation[] annotations = m.getAnnotations();
		ForwardCall call = new ForwardCall(context, annotations, m, target, args, INTERCEPTORS, 0);
		return (T) Lmbd.call(call);
	}

}
