package org.rapidoid.http.fast;

/*
 * #%L
 * rapidoid-http-fast
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
import org.rapidoid.log.Log;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public abstract class AbstractResultHandlingFastHttpHandler extends AbstractFastHttpHandler {

	private final FastHttp http;

	private final byte[] contentType;

	public AbstractResultHandlingFastHttpHandler(FastHttp http, byte[] contentType) {
		this.http = http;
		this.contentType = contentType;
	}

	@Override
	public boolean handle(Channel ctx, boolean isKeepAlive, Map<String, Object> params) {
		Object result;

		try {
			result = handleReq(ctx, params);

			if (result instanceof Callback<?>) {
				ctx.async();
				return true; // async
			}

			if (result == null) {
				return false;
			}

		} catch (Throwable e) {
			Log.error("Error while processing request!", e);
			Throwable cause = UTILS.rootCause(e);
			String errMsg = cause.getMessage();
			byte[] error = errMsg != null ? errMsg.getBytes() : (cause.getClass().getSimpleName() + "!").getBytes();
			http.write500(ctx, isKeepAlive, contentType, error);
			return true;
		}

		writeResult(ctx, isKeepAlive, result);

		return true;
	}

	private void writeResult(Channel ctx, boolean isKeepAlive, Object result) {
		if (contentType.equals(FastHttp.CONTENT_TYPE_JSON)) {
			if (result instanceof byte[]) {
				http.write200(ctx, isKeepAlive, contentType, (byte[]) result);
			} else {
				http.writeSerializedJson(ctx, isKeepAlive, result);
			}
		} else {
			byte[] response = objectToBytes(result);
			http.write200(ctx, isKeepAlive, contentType, response);
		}
	}

	protected void onAsyncResult(Channel ctx, boolean isKeepAlive, Object result) {
		writeResult(ctx, isKeepAlive, result);
		ctx.done();
	}

	private byte[] objectToBytes(Object obj) {
		if (obj instanceof byte[]) {
			return (byte[]) obj;
		} else {
			return obj.toString().getBytes();
		}
	}

	protected abstract Object handleReq(Channel ctx, Map<String, Object> params) throws Exception;

}
