package org.rapidoid.aop;

/*
 * #%L
 * rapidoid-commons
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
import java.util.concurrent.Callable;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Metadata;
import org.rapidoid.cls.Cls;
import org.rapidoid.log.Log;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class ForwardCall implements Callable<Object> {

	private final Object context;
	private final Annotation[] annotations;
	private final Method m;
	private final Object target;
	private final Object[] args;
	private final InterceptorConfig[] interceptors;
	private final int index;

	public ForwardCall(Object context, Annotation[] annotations, Method m, Object target, Object[] args,
			InterceptorConfig[] interceptors, int index) {
		this.context = context;
		this.annotations = annotations;
		this.m = m;
		this.target = target;
		this.args = args;
		this.interceptors = interceptors;
		this.index = index;
	}

	@Override
	public Object call() throws Exception {
		for (int i = index; i < interceptors.length; i++) {

			InterceptorConfig ic = interceptors[i];
			Annotation ann = null;
			boolean any = ic.annotated.isEmpty();

			if (!any) {
				ann = findAnnotation(annotations, ic);
			}

			if (any || ann != null) {
				Callable<Object> forward = new ForwardCall(ic, annotations, m, target, args, interceptors, i + 1);
				Log.debug("Intercepting AOP call", "method", m, "annotation", ann);
				return ic.interceptor.intercept(forward, ann, context, m, target, args);
			}
		}

		return Cls.invoke(m, target, args);
	}

	private static Annotation findAnnotation(Annotation[] annotations, InterceptorConfig ic) {
		for (Class<? extends Annotation> annotated : ic.annotated) {
			Annotation ann = Metadata.get(annotations, annotated);

			if (ann != null) {
				return ann;
			}
		}

		return null;
	}

}
