/*-
 * #%L
 * rapidoid-http-fast
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

package org.rapidoid.http.handler;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.Req;
import org.rapidoid.http.RespBody;
import org.rapidoid.http.impl.BodyRenderer;
import org.rapidoid.http.impl.RespImpl;
import org.rapidoid.jpa.JPA;
import org.rapidoid.u.U;

import java.util.concurrent.atomic.AtomicReference;

@Authors("Nikolche Mihajlovski")
@Since("5.5.1")
public class HttpTxWrapper extends RapidoidThing implements HttpWrapper {

	private final TransactionMode txMode;

	public HttpTxWrapper(TransactionMode txMode) {
		this.txMode = txMode;
	}

	@Override
	public Object wrap(final Req req, final HandlerInvocation invocation) {
		final AtomicReference<Object> result = new AtomicReference<>();

		U.must(txMode != null && txMode != TransactionMode.NONE);

		boolean readOnly = (txMode == TransactionMode.AUTO)
			? HttpUtils.isGetReq(req)
			: txMode == TransactionMode.READ_ONLY;

		try {
			JPA.transaction(new Runnable() {
				@Override
				public void run() {
					Object res;

					try {
						res = invocation.invoke();

					} catch (Exception e) {
						// throw to rollback
						throw U.rte("Error occurred inside the transactional web handler!", e);
					}

					if (res instanceof Throwable) {
						// throw to rollback
						Throwable err = (Throwable) res;
						throw U.rte("Error occurred inside the transactional web handler!", err);

					} else {

						// serialize the result into a HTTP response body, while still inside tx (see #153)
						RespImpl resp = (RespImpl) req.response(); // TODO find a cleaner access
						RespBody body = BodyRenderer.resultToRespBody(resp, res);
						result.set(body);
					}

				}
			}, readOnly);

		} catch (Throwable e) {
			result.set(e);
		}

		return result.get();
	}

}
