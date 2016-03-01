package org.rapidoid.web;

/*
 * #%L
 * rapidoid-web
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
import org.rapidoid.aop.AOPInterceptor;
import org.rapidoid.ctx.Current;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.security.Secure;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class AuthInterceptor implements AOPInterceptor {

	@Override
	public Object intercept(final Callable<Object> forward, Annotation ann, Object ctx, final Method m,
	                        final Object target, final Object[] args) {

		String username = Current.username();
		Set<String> roles = Current.roles();

		if (Secure.canAccessMethod(username, roles, m)) {
			return Lmbd.call(forward);
		} else {
			throw new SecurityException("The user doesn't have the required roles!");
		}
	}

}
