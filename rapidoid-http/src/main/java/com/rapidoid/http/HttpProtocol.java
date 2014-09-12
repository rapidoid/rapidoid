package com.rapidoid.http;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.Connection;
import org.rapidoid.Ctx;
import org.rapidoid.Rapidoid;
import org.rapidoid.buffer.Buf;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.Range;
import org.rapidoid.net.ExchangeProtocol;
import org.rapidoid.net.RapidoidHelper;
import org.rapidoid.util.U;

public class HttpProtocol extends ExchangeProtocol<WebExchangeImpl> {

	private static final byte[] CONTENT_TYPE = "Content-Type".getBytes();

	private static final byte[] MULTIPART_FORM_DATA_BOUNDARY1 = "multipart/form-data; boundary=".getBytes();

	private static final byte[] MULTIPART_FORM_DATA_BOUNDARY2 = "multipart/form-data;boundary=".getBytes();

	private final HttpParser parser = U.inject(HttpParser.class);

	private final Router router;

	private final HttpResponses responses;

	public HttpProtocol(WebConfig config, Router router) {
		super(WebExchangeImpl.class);
		this.router = router;
		this.responses = new HttpResponses(!config.noserver(), !config.noserver());
	}

	@Override
	protected void process(Ctx ctx, WebExchangeImpl web) {

		parser.parse(web.input(), web.isGet, web.isKeepAlive, web.body, web.verb, web.uri, web.path, web.query,
				web.protocol, web.headers, web.helper());

		web.failIf(web.verb.isEmpty() || web.uri.isEmpty(), "Invalid HTTP request!");
		analyzeRequest(web);

		web.respType = 1;

		int posBefore = web.output().size();

		web.output().append(resp(web).bytes());

		try {
			boolean dispatched = router.dispatch(web);
			if (!dispatched) {
				web.write("Invalid HTTP VERB or URL PATH!");
				web.done();
			}
		} catch (Throwable e) {
			U.error("Internal server error!", "request", web, "error", e);
			web.write("Internal server error!");
			web.done();
		}

		long wrote = web.getTotalWritten();
		U.ensure(wrote <= Integer.MAX_VALUE, "Response too big!");

		int pos = posBefore + resp(web).contentLengthPos;

		web.output().putNumAsText(pos, wrote);
	}

	private HttpResponse resp(WebExchangeImpl exchange) {
		HttpResponse resp = responses.get(exchange.isKeepAlive.value, exchange.respType);
		assert resp != null;
		return resp;
	}

	private void analyzeRequest(WebExchangeImpl exchange) {
		Buf src = exchange.input();

		RapidoidHelper helper = exchange.helper();

		if (isMultipartForm(exchange)) {
			helper.bytes[0] = '-';
			helper.bytes[1] = '-';

			src.get(exchange.multipartBoundary, helper.bytes, 2);

//			U.show(src.get(exchange.multipartBoundary));
//			U.show(new String(helper.bytes, 0, exchange.multipartBoundary.length + 2));
//			U.print("# ********************");
//			U.show(src.get(exchange.body));
//			U.print("# ********************");

			parseMultiParts(exchange);
		}
	}

	/* http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2 */
	private void parseMultiParts(WebExchangeImpl exchange) {
		Buf src = exchange.input();

		int start = exchange.body.start;
		int limit = exchange.body.limit();

		int sepLen = exchange.multipartBoundary.length + 2;
		int pos1 = -1, pos2;

		U.print("****** PARSING MULTI  **************");
		try {
			while ((pos2 = src.find(start, limit, exchange.helper().bytes, 0, sepLen, true)) >= 0) {
				U.print("****** PARSE MULTI *** pos2=" + pos2);

				if (pos1 >= 0 && pos2 >= 0) {
					int from = pos1 + sepLen + 2;
					int to = pos2 - 2;
					// U.show(src.get(Range.fromTo(from, to)));

					KeyValueRanges headers = exchange.helper().pairs;
					headers.reset();

					int aa = src.limit();

					parser.parseHeaders(src, from, to, headers);

					src.limit(aa);

					// ?? int bodyStart = src.position(); ??
					// U.show(src.get(Range.fromTo(bodyStart, to)));
				}

				pos1 = pos2;
				start = pos2 + sepLen;
			}

		} catch (Throwable e) {
			exchange.fail("Multipart data parse error!");
			U.warn("Multipart parse error!", e);
		}
	}

	private boolean isMultipartForm(WebExchangeImpl exchange) {
		Buf buf = exchange.input();
		Range contType = exchange.headers().ranges().get(buf, CONTENT_TYPE, false);

		if (contType != null) {
			if (buf.startsWith(contType, MULTIPART_FORM_DATA_BOUNDARY1, false)) {
				exchange.multipartBoundary.setInterval(contType.start + MULTIPART_FORM_DATA_BOUNDARY1.length,
						contType.limit());
				return true;
			}

			if (buf.startsWith(contType, MULTIPART_FORM_DATA_BOUNDARY2, false)) {
				exchange.multipartBoundary.setInterval(contType.start + MULTIPART_FORM_DATA_BOUNDARY2.length,
						contType.limit());
				return true;
			}
		}

		exchange.multipartBoundary.reset();

		return false;
	}

	@Override
	public void before(Connection conn, WebExchangeImpl exchange, int kind) {
		switch (kind) {
		case Rapidoid.WRITE:
			break;

		case WebExchangeImpl.WHOLE:
			break;

		case WebExchangeImpl.HEADER:
			break;

		case WebExchangeImpl.BODY_PART:
			break;

		default:
			throw U.notExpected();
		}

		// conn.output().append(CR_LF);
	}

	@Override
	public void after(Connection conn, WebExchangeImpl exchange, int kind) {
	}

	@Override
	protected void complete(Connection conn, WebExchangeImpl exchange) {
	}

}
