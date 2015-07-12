package org.rapidoid.aop;

/*
 * #%L
 * rapidoid-utils
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
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class AOP {

	private static Map<Class<? extends Annotation>, AOPInterceptor> INTERCEPTORS = U.synchronizedMap();

	public static void register(Class<? extends Annotation> annotated, AOPInterceptor interceptor) {
		INTERCEPTORS.put(annotated, interceptor);
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(Object context, Method m, Object target, Object... args) {
		// simple and limited AOP invocation interceptor, only 1 annotation pet method is supported

		for (Annotation ann : m.getAnnotations()) {
			Class<? extends Annotation> annotation = ann.annotationType();
			AOPInterceptor interceptor = INTERCEPTORS.get(annotation);

			if (interceptor != null) {
				return (T) interceptor.intercept(ann, context, m, target, args);
			}
		}

		return Cls.invoke(m, target, args);
	}

}
