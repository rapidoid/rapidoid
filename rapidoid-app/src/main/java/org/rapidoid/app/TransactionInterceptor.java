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
import java.util.concurrent.atomic.AtomicReference;

import org.rapidoid.annotation.Transaction;
import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.aop.AOPInterceptor;
import org.rapidoid.cls.Cls;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.db.DB;
import org.rapidoid.util.U;

public class TransactionInterceptor implements AOPInterceptor {

	@Override
	public Object intercept(Annotation ann, Object ctx, final Method m, final Object target, final Object... args) {
		HttpExchange x = (HttpExchange) ctx;
		TransactionMode txMode = getTxMode(ann);

		final boolean readOnly;
		if (txMode == TransactionMode.AUTO) {
			U.notNull(x, "HTTP exchange");
			readOnly = x.isGetReq();
		} else {
			readOnly = txMode == TransactionMode.READ_ONLY;
		}

		final AtomicReference<Object> result = new AtomicReference<Object>();

		DB.transaction(new Runnable() {
			@Override
			public void run() {
				result.set(Cls.invoke(m, target, args));
			}
		}, readOnly);

		return result.get();
	}

	private TransactionMode getTxMode(Annotation ann) {
		if (ann instanceof Transaction) {
			Transaction tx = (Transaction) ann;
			TransactionMode txMode = tx.value();
			return txMode;
		} else {
			Log.warn("Providing LIMITED support for this transaction annotation!", "annotation", ann);
			return TransactionMode.AUTO;
		}
	}

}
