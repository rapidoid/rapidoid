package org.rapidoid.http;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.commons.Dates;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.JSON;
import org.rapidoid.http.impl.HttpParser;
import org.rapidoid.http.impl.MaybeReq;
import org.rapidoid.http.impl.lowlevel.HttpIO;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.Server;
import org.rapidoid.net.TCP;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.util.Msc;
import org.rapidoid.writable.ReusableWritable;

@Authors("Nikolche Mihajlovski")
@Since("5.2.1")
public abstract class AbstractHttpServer extends RapidoidThing implements Protocol {

	protected final byte[] STATUS_200 = HttpResponseCodes.get(200);

	protected final byte[] HTTP_404;
	protected final byte[] HTTP_500;

	protected final byte[] CONN_CLOSE_HDR = hdr("Connection: close");
	protected final byte[] SERVER_HDR;

	protected final byte[] DATE_TXT = "Date: ".getBytes();
	protected final byte[] CONTENT_LENGTH_TXT = "Content-Length: ".getBytes();
	protected final byte[] CONTENT_TYPE_TXT = "Content-Type: ".getBytes();

	protected final HttpParser HTTP_PARSER = createParser();

	private final boolean syncBufs;

	public AbstractHttpServer() {
		this("Rapidoid", "Not found!", "Error!", true);
	}

	public AbstractHttpServer(String serverName, String notFoundMsg, String errorMsg, boolean syncBufs) {
		this.SERVER_HDR = hdr("Server: " + serverName);
		this.HTTP_404 = fullResp(404, notFoundMsg.getBytes());
		this.HTTP_500 = fullResp(500, errorMsg.getBytes());
		this.syncBufs = syncBufs;
	}

	private static byte[] hdr(String name) {
		return (name + "\r\n").getBytes();
	}

	protected byte[] fullResp(int code, byte[] content) {
		String status = new String(HttpResponseCodes.get(code));

		String resp = status +
			"Content-Length: " + content.length + "\r\n" +
			"\r\n" + new String(content);

		return resp.getBytes();
	}

	protected HttpParser createParser() {
		return new HttpParser();
	}

	@Override
	public void process(Channel ctx) {
		if (ctx.isInitial()) {
			return;
		}

		Buf buf = ctx.input();
		RapidoidHelper data = ctx.helper();

		HTTP_PARSER.parse(buf, data);

		boolean keepAlive = data.isKeepAlive.value;

		HttpStatus status = handle(ctx, buf, data);

		switch (status) {
			case DONE:
				ctx.closeIf(!keepAlive);
				break;

			case NOT_FOUND:
				ctx.write(HTTP_404);
				ctx.closeIf(!keepAlive);
				break;

			case ERROR:
				ctx.write(HTTP_500);
				ctx.closeIf(!keepAlive);
				break;

			case ASYNC:
				// do nothing
				break;
		}
	}

	protected abstract HttpStatus handle(Channel ctx, Buf buf, RapidoidHelper data);

	protected void startResponse(Channel ctx, boolean isKeepAlive) {
		ctx.write(STATUS_200);
		writeCommonHeaders(ctx, isKeepAlive);
	}

	protected void startResponse(Channel ctx, int code, boolean isKeepAlive) {
		ctx.write(HttpResponseCodes.get(code));
		writeCommonHeaders(ctx, isKeepAlive);
	}

	protected void writeCommonHeaders(Channel ctx, boolean isKeepAlive) {
		if (!isKeepAlive) ctx.write(CONN_CLOSE_HDR);

		ctx.write(SERVER_HDR);

		writeDateHeader(ctx);
	}

	protected void writeDateHeader(Channel ctx) {
		ctx.write(DATE_TXT);
		ctx.write(Dates.getDateTimeBytes());
		ctx.write(CR_LF);
	}

	private void writeContentTypeHeader(Channel ctx, MediaType contentType) {
		ctx.write(CONTENT_TYPE_TXT);
		ctx.write(contentType.getBytes());
		ctx.write(CR_LF);
	}
	
	protected void writeBody(Channel ctx, byte[] body, MediaType contentType) {
		writeBody(ctx, body, 0, body.length, contentType);
	}

	protected void writeBody(Channel ctx, byte[] body, int offset, int length, MediaType contentType) {
		writeContentTypeHeader(ctx, contentType);
		HttpIO.INSTANCE.writeContentLengthHeader(ctx, body.length);

		ctx.write(CR_LF);

		ctx.write(body, offset, length);
	}

	protected void writeJsonBody(MaybeReq req, Channel ctx, Object value) {
		writeContentTypeHeader(ctx, MediaType.JSON);

		ReusableWritable out = Msc.locals().jsonRenderingStream();
		JSON.stringify(value, out);

		HttpIO.INSTANCE.writeContentLengthHeader(ctx, out.size());
		HttpIO.INSTANCE.closeHeaders(req, ctx.output());
		ctx.write(out.array(), 0, out.size());
	}

	protected HttpStatus serializeToJson(MaybeReq req, Channel ctx, boolean isKeepAlive, Object value) {
		startResponse(ctx, isKeepAlive);
		writeJsonBody(req, ctx, value);
		return HttpStatus.DONE;
	}
	
	protected HttpStatus ok(Channel ctx, boolean isKeepAlive, byte[] body, MediaType contentType) {
		return ok(ctx, isKeepAlive, body, 0, body.length, contentType);
	}

	protected HttpStatus ok(Channel ctx, boolean isKeepAlive, byte[] body, int offset, int length, MediaType contentType) {
		startResponse(ctx, isKeepAlive);
		writeBody(ctx, body, offset, length, contentType);
		return HttpStatus.DONE;
	}

	protected HttpStatus plain(Channel ctx, boolean isKeepAlive, byte[] body) {
		return ok(ctx, isKeepAlive, body, MediaType.PLAIN_TEXT_UTF_8);
	}

	protected HttpStatus json(Channel ctx, boolean isKeepAlive, byte[] body) {
		return ok(ctx, isKeepAlive, body, MediaType.JSON);
	}

	protected HttpStatus html(Channel ctx, boolean isKeepAlive, byte[] body) {
		return ok(ctx, isKeepAlive, body, MediaType.HTML_UTF_8);
	}

	protected HttpStatus binary(Channel ctx, boolean isKeepAlive, byte[] body) {
		return ok(ctx, isKeepAlive, body, MediaType.APPLICATION_OCTET_STREAM);
	}

	protected boolean matches(Buf buf, BufRange range, byte[] value) {
		return BytesUtil.matches(buf.bytes(), range, value, true);
	}

	protected boolean matchesIgnoreCase(Buf buf, BufRange range, byte[] value) {
		return BytesUtil.matches(buf.bytes(), range, value, false);
	}

	public Server listen(int port) {
		return listen("0.0.0.0", port);
	}

	public Server listen(String address, int port) {
		return TCP.server()
			.protocol(this)
			.address(address)
			.port(port)
			.syncBufs(syncBufs)
			.build()
			.start();
	}

}
