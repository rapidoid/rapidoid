package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
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

import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchangeImpl;
import org.rapidoid.http.HttpProtocol;
import org.rapidoid.lambda.Callback;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.DB;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

public class TxHelper {

	static void runInTx(final HttpExchange x) {

		TransactionMode txMode = x.getTransactionMode();

		Callback<Void> callback = new Callback<Void>() {
			@Override
			public void onDone(Void result, Throwable error) {
				if (error != null) {
					HttpProtocol.handleError((HttpExchangeImpl) x, error);
				}
				x.done();
			}
		};

		x.async();

		DB.transaction(new Runnable() {
			@Override
			public void run() {
				Object result;

				try {
					result = AppHandler.processReq(x);
				} catch (Exception e) {
					Log.error("Exception occured while processing request inside transaction!", UTILS.rootCause(e));
					throw U.rte(e);
				}

				try {
					HttpProtocol.processResponse(x, result);
				} catch (Exception e) {
					Log.error("Exception occured while finalizing response inside transaction!", UTILS.rootCause(e));
					throw U.rte(e);
				}
			}
		}, txMode == TransactionMode.READ_ONLY, callback);
	}
}
