package org.rapidoid.http.impl.lowlevel;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.data.BufRange;
import org.rapidoid.http.HttpStatus;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Req;
import org.rapidoid.http.impl.MaybeReq;
import org.rapidoid.log.LogLevel;
import org.rapidoid.net.AsyncLogic;
import org.rapidoid.net.abstracts.Channel;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Map;

/*
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpIO extends RapidoidThing {

	public static final HttpIO INSTANCE = new HttpIO();

	private final LowLevelHttpIO impl = new LowLevelHttpIO();

	public void removeTrailingSlash(Buf buf, BufRange range) {
		impl.removeTrailingSlash(buf, range);
	}

	public void writeResponse(MaybeReq req, Channel ctx, boolean isKeepAlive, int code, MediaType contentTypeHeader, byte[] content) {
		impl.writeResponse(req, ctx, isKeepAlive, code, contentTypeHeader, content);
	}

	public void write200(MaybeReq req, Channel ctx, boolean isKeepAlive, MediaType contentTypeHeader, byte[] content) {
		impl.write200(req, ctx, isKeepAlive, contentTypeHeader, content);
	}

	public void error(Req req, Throwable error, LogLevel logLevel) {
		impl.error(req, error, logLevel);
	}

	public HttpStatus errorAndDone(Req req, Throwable error, LogLevel logLevel) {
		return impl.errorAndDone(req, error, logLevel);
	}

	public void writeContentLengthAndBody(MaybeReq req, Channel ctx, ByteArrayOutputStream body) {
		impl.writeContentLengthAndBody(req, ctx, body);
	}

	public void writeContentLengthHeader(Channel ctx, int len) {
		impl.writeContentLengthHeader(ctx, len);
	}

	public void writeHttpResp(MaybeReq req, Channel ctx, boolean isKeepAlive, int code, MediaType contentType, Object value) {
		impl.writeHttpResp(req, ctx, isKeepAlive, code, contentType, value);
	}

	public void done(Req req) {
		impl.done(req);
	}

	public void resume(MaybeReq maybeReq, Channel channel, AsyncLogic logic) {
		impl.resume(maybeReq, channel, logic);
	}

	public void writeBadRequest(Channel channel) {
		impl.writeBadRequest(channel);
	}

	public void respond(MaybeReq maybeReq, Channel channel, long connId, long handle,
	                    int code, boolean isKeepAlive, MediaType contentType,
	                    byte[] body, Map<String, String> headers, Map<String, String> cookies) {

		impl.respond(maybeReq, channel, connId, handle, code, isKeepAlive, contentType, body, headers, cookies);
	}

	public void respond(MaybeReq maybeReq, Channel channel, long connId, long handle,
	                    int code, boolean isKeepAlive, MediaType contentType,
	                    ByteBuffer body, Map<String, String> headers, Map<String, String> cookies) {

		impl.respond(maybeReq, channel, connId, handle, code, isKeepAlive, contentType, body, headers, cookies);
	}


	public void closeHeaders(MaybeReq req, Buf out) {
		impl.closeHeaders(req, out);
	}
}
