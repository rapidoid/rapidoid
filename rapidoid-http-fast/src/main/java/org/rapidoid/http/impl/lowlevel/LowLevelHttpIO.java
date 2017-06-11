package org.rapidoid.http.impl.lowlevel;

import org.rapidoid.RapidoidThing;
import org.rapidoid.activity.RapidoidThreadLocals;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.cls.Cls;
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
import org.rapidoid.http.impl.MaybeReq;
import org.rapidoid.http.impl.ReqImpl;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.GlobalCfg;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.net.AsyncLogic;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.writable.ReusableWritable;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.rapidoid.util.Constants.CR_LF;

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
@Since("5.3.0")
class LowLevelHttpIO extends RapidoidThing {

	private static final byte[] HTTP_200_OK = "HTTP/1.1 200 OK\r\n".getBytes();

	private static final byte[] HTTP_400_BAD_REQUEST = "HTTP/1.1 400 Bad Request\r\nContent-Length: 12\r\n\r\nBad Request!"
		.getBytes();

	private static final byte[] HEADER_SEP = ": ".getBytes();

	private static final byte[] CONN_KEEP_ALIVE = "Connection: keep-alive\r\n".getBytes();

	private static final byte[] CONN_CLOSE = "Connection: close\r\n".getBytes();

	private static final byte[] SERVER_HEADER;

	private static final byte[] CONTENT_LENGTH_IS = "Content-Length: ".getBytes();

	private static final byte[] CONTENT_LENGTH_UNKNOWN = "Content-Length: 0000000000".getBytes();

	private static final int CONTENT_LENGTHS_SIZE = 5000;

	private static final byte[] DATE_IS = "Date: ".getBytes();

	private static final byte[][] CONTENT_LENGTHS = new byte[CONTENT_LENGTHS_SIZE][];

	private static final boolean MANDATORY_HEADER_CONNECTION;
	private static final boolean MANDATORY_HEADER_DATE;
	private static final boolean MANDATORY_HEADER_SERVER;
	private static final boolean MANDATORY_HEADER_CONTENT_TYPE;

	private static final byte[] UNIFORM_DATE = "Sat, 10 Sep 2016 01:02:03 GMT".getBytes();

	private static final AtomicLong ASYNC_ID_GEN = new AtomicLong();

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

	LowLevelHttpIO() {
	}

	void removeTrailingSlash(Buf buf, BufRange range) {
		if (range.length > 1 && buf.get(range.last()) == '/') {
			range.length--;
		}
	}

	void startResponse(Channel ctx, int code, boolean isKeepAlive, MediaType contentType) {
		ctx.write(code == 200 ? HTTP_200_OK : HttpResponseCodes.get(code));
		addDefaultHeaders(ctx, isKeepAlive, contentType);
	}

	private void addDefaultHeaders(Channel ctx, boolean isKeepAlive, MediaType contentType) {

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

	void addCustomHeader(Channel ctx, byte[] name, byte[] value) {
		ctx.write(name);
		ctx.write(HEADER_SEP);
		ctx.write(value);
		ctx.write(CR_LF);
	}

	void writeResponse(final MaybeReq req, final Channel ctx, final boolean isKeepAlive, final int code, final MediaType contentTypeHeader, final byte[] content) {
		startResponse(ctx, code, isKeepAlive, contentTypeHeader);
		writeContentLengthAndBody(req, ctx, content);
	}

	void write200(MaybeReq req, Channel ctx, boolean isKeepAlive, MediaType contentTypeHeader, byte[] content) {
		writeResponse(req, ctx, isKeepAlive, 200, contentTypeHeader, content);
	}

	void error(final Req req, final Throwable error, LogLevel logLevel) {
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

	private void logError(Req req, Throwable error, LogLevel logLevel) {

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

		Log.log(null, logLevel, "Error occurred when handling request!", "error", error);
	}

	HttpStatus errorAndDone(final Req req, final Throwable error, final LogLevel logLevel) {

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

	void writeContentLengthAndBody(final MaybeReq req, final Channel ctx, final byte[] body) {
		writeContentLengthHeader(ctx, body.length);
		closeHeaders(req, ctx.output());
		ctx.write(body);
	}

	void writeContentLengthAndBody(final MaybeReq req, final Channel ctx, final ByteArrayOutputStream body) {
		writeContentLengthHeader(ctx, body.size());
		closeHeaders(req, ctx.output());
		ctx.output().append(body);
	}

	void writeContentLengthHeader(Channel ctx, long contentLength) {
		if (contentLength < CONTENT_LENGTHS_SIZE) {
			ctx.write(CONTENT_LENGTHS[(int) contentLength]);

		} else {
			ctx.write(CONTENT_LENGTH_IS);
			Buf out = ctx.output();
			out.putNumAsText(out.size(), contentLength, true);
			ctx.write(CR_LF);
		}
	}

	void writeAsJson(final MaybeReq req, final Channel ctx, final int code, final boolean isKeepAlive, final Object value) {
		startResponse(ctx, code, isKeepAlive, MediaType.JSON);

		RapidoidThreadLocals locals = Msc.locals();

		ReusableWritable out = locals.jsonRenderingStream();
		JSON.stringify(value, out);

		writeContentLengthHeader(ctx, out.size());
		closeHeaders(req, ctx.output());

		ctx.write(out.array(), 0, out.size());
	}

	@SuppressWarnings("unused")
	private void writeOnBufferAsJson(MaybeReq req, Channel ctx, int code, boolean isKeepAlive, Object value) {
		startResponse(ctx, code, isKeepAlive, MediaType.JSON);

		Buf output = ctx.output();

		synchronized (output) {
			writeJsonBody(req, output.unwrap(), value);
		}
	}

	private void writeJsonBody(MaybeReq req, Buf out, Object value) {
		// Content-Length header
		out.append(CONTENT_LENGTH_UNKNOWN);
		int posConLen = out.size() - 1;
		out.append(CR_LF);

		closeHeaders(req, out);

		int posBefore = out.size();

		JSON.stringify(value, out.asOutputStream());

		int posAfter = out.size();
		int contentLength = posAfter - posBefore;

		out.putNumAsText(posConLen, contentLength, false);
	}

	void closeHeaders(MaybeReq req, Buf out) {
		// finishing the headers
		out.append(CR_LF);

		ReqImpl reqq = (ReqImpl) req.getReqOrNull();

		if (reqq != null) {
			U.must(reqq.channel().output() == out);
			reqq.onHeadersCompleted();
		}
	}

	void done(Req req) {
		ReqImpl reqq = (ReqImpl) req;
		reqq.doneProcessing();

		Channel channel = reqq.channel();
		channel.send();

		channel.closeIf(!reqq.isKeepAlive());
	}

	void resume(MaybeReq maybeReq, Channel channel, AsyncLogic logic) {
		Req req = maybeReq.getReqOrNull();

		if (req != null) {
			channel.resume(req.connectionId(), req.handle(), logic);
		} else {
			logic.resumeAsync();
		}
	}

	void writeBadRequest(Channel channel) {
		channel.write(HTTP_400_BAD_REQUEST);
		channel.close();
	}

	void respond(final MaybeReq maybeReq, final Channel channel, long connId, long handle,
	             final int code, final boolean isKeepAlive, final MediaType contentType, final Object body,
	             final Map<String, String> headers, final Map<String, String> cookies) {

		final ReqImpl req = (ReqImpl) maybeReq.getReqOrNull();

		if (handle < 0) {
			if (req != null) {
				handle = req.handle();
			} else {
				handle = channel.handle();
			}
		}

		if (connId < 0) {
			if (req != null) {
				connId = req.connectionId();
			} else {
				connId = channel.connId();
			}
		}

		final long id = ASYNC_ID_GEN.incrementAndGet();

		channel.resume(connId, handle, new AsyncLogic() {

			@Override
			public String toString() {
				String bb = (body instanceof byte[]) ? new String((byte[]) body) : U.str(body);
				return U.str(U.join(":", "#" + id, channel, code, bb, isKeepAlive, contentType));
			}

			@Override
			public boolean resumeAsync() {

				boolean complete;

				startResponse(channel, code, isKeepAlive, contentType);

				if (U.notEmpty(headers)) {
					for (Map.Entry<String, String> e : headers.entrySet()) {
						addCustomHeader(channel, e.getKey().getBytes(), e.getValue().getBytes());
					}
				}

				if (U.notEmpty(cookies)) {
					for (Map.Entry<String, String> e : cookies.entrySet()) {
						String cookie = e.getKey() + "=" + e.getValue();
						addCustomHeader(channel, HttpHeaders.SET_COOKIE.getBytes(), cookie.getBytes());
					}
				}

				Buf output = channel.output();

				synchronized (channel) {
					if (body == null) {

						int posContentLengthValue = output.size() - 1;

						// finishing the headers
						closeHeaders(maybeReq, output);

						long posBeforeBody = output.size();

						if (req != null) {
							req.responded(posContentLengthValue, posBeforeBody, false);
						}

						complete = false;

					} else {

						if (body instanceof byte[]) {
							byte[] bytes = (byte[]) body;

							writeContentLengthHeader(channel, bytes.length);
							closeHeaders(maybeReq, output);
							channel.write(bytes);

						} else if (body instanceof ByteBuffer) {
							ByteBuffer buf = (ByteBuffer) body;

							writeContentLengthHeader(channel, buf.remaining());
							closeHeaders(maybeReq, output);
							channel.write(buf);

						} else {
							throw U.rte("Invalid response body type: %s", Cls.of(body));
						}

						if (req != null) {
							req.completed(true);
							done(req);
						}

						complete = true;
					}
				}

				return complete;
			}
		});
	}

}
