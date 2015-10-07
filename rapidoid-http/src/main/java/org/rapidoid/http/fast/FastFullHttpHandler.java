package org.rapidoid.http.fast;

/*
 * #%L
 * rapidoid-http
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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.http.AsyncRawHandler;
import org.rapidoid.net.abstracts.Channel;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class FastFullHttpHandler extends AbstractResultHandlingFastHttpHandler {

	private final AsyncRawHandler handler;

	public FastFullHttpHandler(FastHttp http, byte[] contentType, AsyncRawHandler handler) {
		super(http, contentType);
		this.handler = handler;
	}

	@Override
	protected Object handleReq(final Channel ctx, Map<String, Object> params) throws Exception {
		Callback<Object> ret = new Callback<Object>() {
			@Override
			public void onDone(Object result, Throwable error) throws Exception {
				// FIXME handle error
				onAsyncResult(ctx, true, result);
			}
		};
		return handler.handle(params, ctx.output().asOutputStream(), ret);
	}

	@Override
	public boolean needsParams() {
		return true;
	}

}
