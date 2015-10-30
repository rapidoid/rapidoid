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
import java.util.concurrent.Callable;

import org.rapidoid.annotation.Transaction;
import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.aop.AOPInterceptor;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.lambda.Lambdas;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.db.DB;
import org.rapidoid.u.U;

public class TransactionInterceptor implements AOPInterceptor {

	@Override
	public Object intercept(final Callable<Object> forward, Annotation ann, Object ctx, final Method m,
			final Object target, final Object[] args) {

		final HttpExchange x = Ctxs.ctx().exchange();
		TransactionMode txMode = getTxMode(ann);

		final boolean readOnly;
		if (txMode == TransactionMode.AUTO) {
			U.notNull(x, "HTTP exchange");
			readOnly = x.isGetReq();
		} else {
			readOnly = txMode == TransactionMode.READ_ONLY;
		}

		x.async();

		DB.transaction(new Runnable() {
			@Override
			public void run() {
				x.result(Lambdas.call(forward));
			}
		}, readOnly, new Callback<Void>() {

			@Override
			public void onDone(Void result, Throwable error) throws Exception {
				if (error != null) {
					x.error(error);
				}
				x.done();
			}
		});

		return x;
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
