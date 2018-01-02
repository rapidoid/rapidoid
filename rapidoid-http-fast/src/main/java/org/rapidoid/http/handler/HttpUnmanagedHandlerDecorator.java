/*-
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.*;
import org.rapidoid.http.impl.MaybeReq;
import org.rapidoid.http.impl.ReqImpl;
import org.rapidoid.http.impl.lowlevel.HttpIO;
import org.rapidoid.log.Log;
import org.rapidoid.net.abstracts.Channel;

@Authors("Nikolche Mihajlovski")
@Since("5.5.1")
public class HttpUnmanagedHandlerDecorator extends AbstractHttpHandlerDecorator {

	private final MediaType contentType;

	HttpUnmanagedHandlerDecorator(AbstractDecoratingHttpHandler handler, FastHttp http, MediaType contentType) {
		super(handler, http);
		this.contentType = contentType;
	}

	@Override
	public final HttpStatus handle(Channel ctx, boolean isKeepAlive, Req req) {
		Object result;
		MaybeReq maybeReq = HttpUtils.maybe(req);

		ReqImpl reqq = (ReqImpl) req;

		// handle & post-process
		result = handleReqAndPostProcess(ctx, isKeepAlive, req);

		// the response properties might be overwritten
		int code;
		MediaType ctype;

		if (req != null && reqq.hasResponseAttached()) {
			Resp resp = req.response();
			ctype = resp.contentType();
			code = resp.code();

		} else {
			ctype = contentType;
			code = 200;
		}

		if (result == HttpStatus.NOT_FOUND) {
			http.notFound(ctx, isKeepAlive, ctype, handler, req);
			return HttpStatus.NOT_FOUND;
		}

		if (result == HttpStatus.ASYNC) {
			return HttpStatus.ASYNC;
		}

		if (result instanceof Throwable) {
			Throwable err = (Throwable) result;
			HttpIO.INSTANCE.writeHttpResp(maybeReq, ctx, isKeepAlive, 500, MediaType.PLAIN_TEXT_UTF_8, "Internal server error!".getBytes());

			Log.error("Error occurred during unmanaged request processing", "request", req, "error", err);
			return HttpStatus.DONE;
		}

		HttpIO.INSTANCE.writeHttpResp(maybeReq, ctx, isKeepAlive, code, ctype, result);

		return HttpStatus.DONE;
	}

}
