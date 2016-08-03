package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.commons.Dates;
import org.rapidoid.commons.MediaType;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.With;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.JSON;
import org.rapidoid.http.*;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.util.Constants;

/*
 * #%L
 * rapidoid-http-fast
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpIO extends RapidoidThing {

	public static final byte[] HTTP_200_OK = "HTTP/1.1 200 OK\r\n".getBytes();

	public static final byte[] HTTP_400_BAD_REQUEST = "HTTP/1.1 400 Bad Request\r\nContent-Length: 12\r\n\r\nBad Request!"
		.getBytes();

	private static final byte[] HEADER_SEP = ": ".getBytes();

	private static final byte[] CONN_KEEP_ALIVE = "Connection: keep-alive\r\n".getBytes();

	private static final byte[] CONN_CLOSE = "Connection: close\r\n".getBytes();

	private static final byte[] SERVER_HEADER = "Server: Rapidoid\r\n".getBytes();

	private static final byte[] CONTENT_LENGTH_IS = "Content-Length: ".getBytes();

	static final byte[] CONTENT_LENGTH_UNKNOWN = "Content-Length: 0000000000".getBytes();

	private static final int CONTENT_LENGTHS_SIZE = 5000;

	private static final byte[] DATE_IS = "Date: ".getBytes();

	private static final byte[][] CONTENT_LENGTHS = new byte[CONTENT_LENGTHS_SIZE][];

	public static final byte[] PAGE_NOT_FOUND = "Page not found!".getBytes();

	static {
		for (int len = 0; len < CONTENT_LENGTHS.length; len++) {
			CONTENT_LENGTHS[len] = (new String(CONTENT_LENGTH_IS) + len + new String(Constants.CR_LF)).getBytes();
		}

		HttpResponseCodes.init();
	}

	private HttpIO() {
	}

	public static void removeTrailingSlash(Buf buf, BufRange range) {
		if (range.length > 1 && buf.get(range.last()) == '/') {
			range.length--;
		}
	}

	public static void startResponse(Channel ctx, int code, boolean isKeepAlive, MediaType contentType) {
		ctx.write(code == 200 ? HTTP_200_OK : HttpResponseCodes.get(code));
		addDefaultHeaders(ctx, isKeepAlive, contentType);
	}

	public static void addDefaultHeaders(Channel ctx, boolean isKeepAlive, MediaType contentType) {
		ctx.write(isKeepAlive ? CONN_KEEP_ALIVE : CONN_CLOSE);

		ctx.write(SERVER_HEADER);

		ctx.write(DATE_IS);
		ctx.write(Dates.getDateTimeBytes());
		ctx.write(Constants.CR_LF);

		ctx.write(contentType.asHttpHeader());
	}

	public static void addCustomHeader(Channel ctx, byte[] name, byte[] value) {
		ctx.write(name);
		ctx.write(HEADER_SEP);
		ctx.write(value);
		ctx.write(Constants.CR_LF);
	}

	public static void write200(Channel ctx, boolean isKeepAlive, MediaType contentTypeHeader, byte[] content) {
		startResponse(ctx, 200, isKeepAlive, contentTypeHeader);
		writeContentLengthAndBody(ctx, content);
	}

	public static void error(final Req req, final Throwable error) {
		Log.debug("HTTP handler error!", "error", error);

		try {
			Resp resp = req.response().code(500);
			Object result = Customization.of(req).errorHandler().handleError(req, resp, error);
			result = HttpUtils.postprocessResult(req, result);
			HttpUtils.resultToResponse(req, result);

		} catch (Exception e) {
			Log.error("An error occurred inside the error handler!", e);
			HttpUtils.resultToResponse(req, HttpUtils.getErrorInfo(req.response(), e));
		}
	}

	public static HttpStatus errorAndDone(final Req req, final Throwable error) {

		req.revert();
		req.async();

		Runnable errorHandler = new Runnable() {
			@Override
			public void run() {
				error(req, error);
				// the Req object will do the rendering
				req.done();
			}
		};

		Ctx ctx = Ctxs.get();

		if (ctx == null) {
			With.exchange(req).run(errorHandler);
		} else {
			Jobs.execute(errorHandler);
		}

		return HttpStatus.ASYNC;
	}

	public static void writeContentLengthAndBody(Channel ctx, byte[] content) {
		int len = content.length;

		if (len < CONTENT_LENGTHS_SIZE) {
			ctx.write(CONTENT_LENGTHS[len]);
		} else {
			ctx.write(CONTENT_LENGTH_IS);
			Buf out = ctx.output();
			out.putNumAsText(out.size(), len, true);
			ctx.write(Constants.CR_LF);
		}

		ctx.write(Constants.CR_LF);
		ctx.write(content);
	}

	public static void writeAsJson(Channel ctx, int code, boolean isKeepAlive, Object value) {
		startResponse(ctx, code, isKeepAlive, MediaType.JSON_UTF_8);

		Buf out = ctx.output();

		ctx.write(CONTENT_LENGTH_UNKNOWN);

		int posConLen = out.size() - 1;
		ctx.write(Constants.CR_LF);

		// finishing the headers
		ctx.write(Constants.CR_LF);

		int posBefore = out.size();

		JSON.stringify(value, out.asOutputStream());

		int posAfter = out.size();
		int contentLength = posAfter - posBefore;

		out.putNumAsText(posConLen, contentLength, false);
	}

	public static void writeContentLengthUnknown(Channel channel) {
		channel.write(HttpIO.CONTENT_LENGTH_UNKNOWN);
	}

	public static void done(Channel ctx, boolean isKeepAlive) {
		ctx.done();
		ctx.closeIf(!isKeepAlive);
	}

}
