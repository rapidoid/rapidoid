package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.commons.Dates;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.With;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.JSON;
import org.rapidoid.http.*;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.GlobalCfg;
import org.rapidoid.util.Msc;
import org.rapidoid.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
public class HttpIO extends RapidoidThing implements Constants {

	public static final byte[] HTTP_200_OK = "HTTP/1.1 200 OK\r\n".getBytes();

	public static final byte[] HTTP_400_BAD_REQUEST = "HTTP/1.1 400 Bad Request\r\nContent-Length: 12\r\n\r\nBad Request!"
		.getBytes();

	private static final byte[] HEADER_SEP = ": ".getBytes();

	private static final byte[] CONN_KEEP_ALIVE = "Connection: keep-alive\r\n".getBytes();

	private static final byte[] CONN_CLOSE = "Connection: close\r\n".getBytes();

	private static final byte[] SERVER_HEADER;

	private static final byte[] CONTENT_LENGTH_IS = "Content-Length: ".getBytes();

	static final byte[] CONTENT_LENGTH_UNKNOWN = "Content-Length: 0000000000".getBytes();

	private static final int CONTENT_LENGTHS_SIZE = 5000;

	private static final byte[] DATE_IS = "Date: ".getBytes();

	private static final byte[][] CONTENT_LENGTHS = new byte[CONTENT_LENGTHS_SIZE][];

	private static final boolean MANDATORY_HEADER_CONNECTION;
	private static final boolean MANDATORY_HEADER_DATE;
	private static final boolean MANDATORY_HEADER_SERVER;
	private static final boolean MANDATORY_HEADER_CONTENT_TYPE;

	private static final byte[] UNIFORM_DATE = "Sat, 10 Sep 2016 01:02:03 GMT".getBytes();

	static {
		for (int len = 0; len < CONTENT_LENGTHS.length; len++) {
			CONTENT_LENGTHS[len] = (new String(CONTENT_LENGTH_IS) + len + new String(CR_LF)).getBytes();
		}

		HttpResponseCodes.init();

		String serverName = Conf.HTTP.entry("serverName").or("Rapidoid");
		SERVER_HEADER = ("Server: " + serverName + "\r\n").getBytes();

		Config mandatoryHeaders = Conf.HTTP.sub("mandatoryHeaders");

		MANDATORY_HEADER_CONNECTION = mandatoryHeaders.entry("connection").or(true);
		MANDATORY_HEADER_DATE = mandatoryHeaders.entry("date").or(true);
		MANDATORY_HEADER_SERVER = mandatoryHeaders.entry("server").or(true);
		MANDATORY_HEADER_CONTENT_TYPE = mandatoryHeaders.entry("contentType").or(true);
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

		if (!isKeepAlive || MANDATORY_HEADER_CONNECTION) {
			ctx.write(isKeepAlive ? CONN_KEEP_ALIVE : CONN_CLOSE);
		}

		if (MANDATORY_HEADER_SERVER) {
			ctx.write(SERVER_HEADER);
		}

		if (MANDATORY_HEADER_DATE) {
			ctx.write(DATE_IS);

			if (!GlobalCfg.uniformOutput()) {
				ctx.write(Dates.getDateTimeBytes());
			} else {
				ctx.write(UNIFORM_DATE);
			}

			ctx.write(CR_LF);
		}

		if (MANDATORY_HEADER_CONTENT_TYPE) {
			ctx.write(contentType.asHttpHeader());
		}
	}

	public static void addCustomHeader(Channel ctx, byte[] name, byte[] value) {
		ctx.write(name);
		ctx.write(HEADER_SEP);
		ctx.write(value);
		ctx.write(CR_LF);
	}

	public static void writeResponse(Channel ctx, boolean isKeepAlive, int code, MediaType contentTypeHeader, byte[] content) {
		startResponse(ctx, code, isKeepAlive, contentTypeHeader);
		writeContentLengthAndBody(ctx, content);
	}

	public static void write200(Channel ctx, boolean isKeepAlive, MediaType contentTypeHeader, byte[] content) {
		writeResponse(ctx, isKeepAlive, 200, contentTypeHeader, content);
	}

	public static void error(final Req req, final Throwable error, LogLevel logLevel) {
		try {
			logError(req, error, logLevel);

			Resp resp = req.response().code(500).result(null);
			Object result = Customization.of(req).errorHandler().handleError(req, resp, error);

			result = HttpUtils.postprocessResult(req, result);
			HttpUtils.resultToResponse(req, result);

		} catch (Exception e) {
			Log.error("An error occurred inside the error handler!", e);
			HttpUtils.resultToResponse(req, HttpUtils.getErrorInfo(req.response(), e));
		}
	}

	private static void logError(Req req, Throwable error, LogLevel logLevel) {

		if (error instanceof NotFound) return;

		if (Msc.isValidationError(error)) {
			if (Log.isDebugEnabled()) {
				Log.debug("Validation error when handling request: " + req);
				error.printStackTrace();
			}
			return;
		}

		if (error instanceof SecurityException) {
			Log.warn("Access denied for request: " + req, "client", req.clientIpAddress());
			return;
		}

		String msg = "Error occurred when handling request!";

		switch (logLevel) {

			// FIXME add proper support e.g. Log.msg(logLevel...)

			case TRACE:
				Log.trace(msg, "error", error);
				break;

			case DEBUG:
				Log.debug(msg, "error", error);
				break;

			case INFO:
				Log.info(msg, "error", error);
				break;

			case WARN:
				Log.warn(msg, "error", error);
				break;

			case ERROR:
				Log.error(msg, "error", error);
				break;
		}
	}

	public static HttpStatus errorAndDone(final Req req, final Throwable error, final LogLevel logLevel) {

		req.revert();
		req.async();

		Runnable errorHandler = new Runnable() {
			@Override
			public void run() {
				error(req, error, logLevel);
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
		writeContentLengthHeader(ctx, content.length);
		ctx.write(CR_LF);
		ctx.write(content);
	}

	public static void writeContentLengthAndBody(Channel ctx, ByteArrayOutputStream baos) {
		writeContentLengthHeader(ctx, baos.size());
		ctx.write(CR_LF);
		ctx.output().append(baos);
	}

	public static void writeContentLengthHeader(Channel ctx, int len) {
		if (len < CONTENT_LENGTHS_SIZE) {
			ctx.write(CONTENT_LENGTHS[len]);
		} else {
			ctx.write(CONTENT_LENGTH_IS);
			Buf out = ctx.output();
			out.putNumAsText(out.size(), len, true);
			ctx.write(CR_LF);
		}
	}

	public static void writeAsJson(Channel ctx, int code, boolean isKeepAlive, Object value) {
		startResponse(ctx, code, isKeepAlive, MediaType.JSON);

		ByteArrayOutputStream os = Msc.locals().jsonRenderingStream();

		JSON.stringify(value, os);
		byte[] arr = os.toByteArray();

		writeContentLengthAndBody(ctx, arr);
	}

	@SuppressWarnings("unused")
	private static void writeOnBufferAsJson(Channel ctx, int code, boolean isKeepAlive, Object value) {
		startResponse(ctx, code, isKeepAlive, MediaType.JSON);

		Buf output = ctx.output();

		synchronized (output) {
			writeJsonBody(output.unwrap(), value);
		}
	}

	public static void writeJsonBody(Buf out, Object value) {
		out.append(CONTENT_LENGTH_UNKNOWN);

		int posConLen = out.size() - 1;
		out.append(CR_LF);

		// finishing the headers
		out.append(CR_LF);

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

	public static void writeNum(Channel ctx, int value) {
		try {
			StreamUtils.putNumAsText(ctx.output().asOutputStream(), value);
		} catch (IOException e) {
			throw U.rte(e);
		}
	}

}
