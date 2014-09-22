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

import org.rapidoid.buffer.Buf;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;
import org.rapidoid.wrap.Bool;
import org.rapidoid.wrap.Int;

public class HttpParser implements Constants {

	private static final int PREFIX_CONN = U.bytesToInt("Conn");

	private static final byte[] CONNECTION = "Connection:".getBytes();

	private static final byte[] CLOSE = "close".getBytes();

	private static final byte[] KEEP_ALIVE = "keep-alive".getBytes();

	private static final byte[] CONTENT_LENGTH = "Content-Length:".getBytes();

	private static final byte[] COOKIE = "Cookie".getBytes();

	private static final byte[] MULTIPART_FORM_DATA_BOUNDARY1 = "multipart/form-data; boundary=".getBytes();

	private static final byte[] MULTIPART_FORM_DATA_BOUNDARY2 = "multipart/form-data;boundary=".getBytes();

	private static final byte[] CONTENT_TYPE = "Content-Type".getBytes();

	private static final byte[] CONTENT_DISPOSITION = "Content-Disposition".getBytes();

	private static final byte[] FORM_DATA = "form-data;".getBytes();

	private static final byte[] NAME_EQ = "name=".getBytes();

	private static final byte[] FILENAME_EQ = "filename=".getBytes();

	private static final byte[] CHARSET_EQ = "charset=".getBytes();

	private static final byte[] _UTF_8 = "UTF-8".getBytes();

	private static final byte[] CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding".getBytes();

	private static final byte[] _7BIT = "7bit".getBytes();

	private static final byte[] _8BIT = "8bit".getBytes();

	private static final byte[] BINARY = "binary".getBytes();

	public void parse(Buf buf, Bool isGet, Bool isKeepAlive, Range body, Range verb, Range uri, Range path,
			Range query, Range protocol, Ranges headers, RapidoidHelper helper) {

		int pos = buf.position();

		boolean getReq = buf.next() == 'G' && buf.next() == 'E' && buf.next() == 'T' && buf.next() == ' ';
		isGet.value = getReq;

		if (getReq) {
			verb.set(0, 3);
		} else {
			buf.position(pos);
			buf.scanUntil(SPACE, verb, true);
		}

		buf.scanUntil(SPACE, uri, true);
		buf.scanLn(protocol, true);

		Int result = helper.integers[0];
		buf.scanLnLn(headers, PREFIX_CONN, result);

		int connPos = result.value;

		isKeepAlive.value = isKeepAlive(buf, headers, connPos);

		buf.split(uri, ASTERISK, path, query, false);

		if (!getReq) {
			parseBody(buf, body, headers, helper);
		}
	}

	private boolean isKeepAlive(Buf buf, Ranges headers, int connPos) {
		if (connPos >= 0) {
			Range connHdr = headers.ranges[connPos];

			if (!buf.startsWith(connHdr, CONNECTION, true)) {
				connHdr = headers.getByPrefix(buf, CONNECTION, false);
			}

			return getKeepAliveValue(buf, connHdr);
		}

		return true;
	}

	private boolean getKeepAliveValue(Buf buf, Range connHdr) {
		if (buf.containsAt(connHdr, CONNECTION.length + 1, KEEP_ALIVE, true)) {
			return true;
		}

		if (buf.containsAt(connHdr, CONNECTION.length + 1, CLOSE, false)) {
			return false;
		}

		if (buf.containsAt(connHdr, CONNECTION.length, CLOSE, false)) {
			return false;
		}

		return true;
	}

	private void parseBody(Buf buf, Range body, Ranges headers, RapidoidHelper helper) {
		Range clen = headers.getByPrefix(buf, CONTENT_LENGTH, false);

		if (clen != null) {
			Range clenValue = helper.ranges5.ranges[helper.ranges5.ranges.length - 1];
			clenValue.setInterval(clen.start + CONTENT_LENGTH.length, clen.limit());
			buf.trim(clenValue);
			long len = buf.getN(clenValue);
			U.ensure(len >= 0 && len <= Integer.MAX_VALUE, "Invalid body size!");
			buf.scanN((int) len, body);
			U.debug("Request body complete", "range", body);
		}
	}

	public void parseParams(Buf buf, KeyValueRanges params, Range range) {
		int pos = buf.position();
		int limit = buf.limit();

		buf.position(range.start);
		buf.limit(range.limit());

		while (buf.hasRemaining()) {
			int ind = params.add();
			int which = buf.scanTo(EQ, AMP, params.keys[ind], false);
			if (which == 1) {
				buf.scanTo(AMP, params.values[ind], false);
			}
		}

		buf.position(pos);
		buf.limit(limit);
	}

	public int parseHeaders(Buf buf, int from, int to, KeyValueRanges headersKV, RapidoidHelper helper) {
		int pos = buf.position();
		int limit = buf.limit();

		buf.position(from);
		buf.limit(to);

		Int result = helper.integers[0];

		Ranges headers = helper.ranges2;
		buf.scanLnLn(headers, 0, result);

		parseHeadersIntoKV(buf, headers, headersKV, null, helper);

		int bodyPos = buf.position();

		buf.position(pos);
		buf.limit(limit);

		return bodyPos;
	}

	public void parseHeadersIntoKV(Buf buf, Ranges headers, KeyValueRanges headersKV, KeyValueRanges cookies,
			RapidoidHelper helper) {

		Range cookie = helper.ranges5.ranges[0];

		for (int i = 0; i < headers.count; i++) {
			Range hdr = headers.ranges[i];
			int ind = headersKV.add();
			Range key = headersKV.keys[ind];
			Range val = headersKV.values[ind];

			boolean split = buf.split(hdr, COL, key, val, true);
			U.ensure(split, "Invalid HTTP header!");

			if (cookies != null && buf.matches(key, COOKIE, false)) {
				headersKV.count--; // don't include cookies in headers

				do {
					buf.split(val, SEMI_COL, cookie, val, true);
					int cind = cookies.add();
					buf.split(cookie, EQ, cookies.keys[cind], cookies.values[cind], true);
				} while (!val.isEmpty());
			}
		}
	}

	public void parseBody(Buf src, KeyValueRanges headers, Range body, KeyValueRanges data, KeyValueRanges files,
			RapidoidHelper helper) {

		Range multipartBoundary = helper.ranges5.ranges[0];

		if (isMultipartForm(src, headers, multipartBoundary)) {
			helper.bytes[0] = '-';
			helper.bytes[1] = '-';

			src.get(multipartBoundary, helper.bytes, 2);

			U.failIf(multipartBoundary.isEmpty(), "Invalid multi-part HTTP request!");

			parseMultiParts(src, body, data, files, multipartBoundary, helper);
		}

	}

	/* http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2 */
	private void parseMultiParts(Buf src, Range body, KeyValueRanges data, KeyValueRanges files,
			Range multipartBoundary, RapidoidHelper helper) {

		int start = body.start;
		int limit = body.limit();

		int sepLen = multipartBoundary.length + 2;
		int pos1 = -1, pos2;

		try {

			while ((pos2 = src.find(start, limit, helper.bytes, 0, sepLen, true)) >= 0) {
				// U.print("****** PARSE MULTI *** pos2=" + pos2);

				if (pos1 >= 0 && pos2 >= 0) {
					int from = pos1 + sepLen + 2;
					int to = pos2 - 2;
					parseMultiPart(src, body, data, files, multipartBoundary, helper, from, to);
				}

				pos1 = pos2;
				start = pos2 + sepLen;
			}

		} catch (Throwable e) {
			U.warn("Multipart parse error!", e);
			throw U.rte("Multipart data parse error!");
		}
	}

	private void parseMultiPart(Buf src, Range body, KeyValueRanges data, KeyValueRanges files,
			Range multipartBoundary, RapidoidHelper helper, int from, int to) {

		KeyValueRanges headers = helper.pairs;
		Range partBody = helper.ranges4.ranges[0];
		Range contType = helper.ranges4.ranges[1];
		Range contEnc = helper.ranges4.ranges[2];
		Range dispo1 = helper.ranges4.ranges[3];
		Range dispo2 = helper.ranges4.ranges[4];
		Range name = helper.ranges4.ranges[5];
		Range filename = helper.ranges4.ranges[6];
		Range charset = helper.ranges4.ranges[7];

		headers.reset();

		int bodyPos = parseHeaders(src, from, to, headers, helper);
		partBody.setInterval(bodyPos, to);

		// form-data; name="a" | form-data; name="f2"; filename="test2.txt"
		Range disposition = headers.get(src, CONTENT_DISPOSITION, false);

		if (src.startsWith(disposition, FORM_DATA, false)) {
			disposition.strip(FORM_DATA.length, 0);
		} else {
			return;
		}

		src.split(disposition, SEMI_COL, dispo1, dispo2, true);

		if (!parseDisposition(src, dispo1, dispo2, name, filename)) {
			if (!parseDisposition(src, dispo2, dispo1, name, filename)) {
				throw U.rte("Unrecognized Content-disposition header!");
			}
		}

		// (OPTIONAL) e.g. application/octet-stream | text/plain;
		// charset=ISO-8859-1 | image/svg+xml | text/plain; charset=utf-8 |
		// | multipart/mixed; boundary=BbC04y | application/pdf |
		// application/vnd.oasis.opendocument.text | image/gif |
		// video/mp4; codecs="avc1.640028 | DEFAULT=text/plain
		Range contentType = headers.get(src, CONTENT_TYPE, false);

		charset.reset();
		contType.reset();
		contEnc.reset();

		if (contentType != null) {
			src.split(contentType, SEMI_COL, contType, contEnc, true);
			if (src.startsWith(contEnc, CHARSET_EQ, false)) {
				charset.assign(contEnc);
				charset.strip(CHARSET_EQ.length, 0);
				src.trim(charset);

				U.failIf(!src.matches(charset, _UTF_8, false), "Only the UTF-8 charset is supported!");
			}
		}

		// (OPTIONAL) e.g. 7bit | 8bit | binary | DEFAULT=7bit
		Range encoding = headers.get(src, CONTENT_TRANSFER_ENCODING, false);

		if (encoding != null) {
			boolean validEncoding = src.matches(encoding, _7BIT, false) || src.matches(encoding, _8BIT, false)
					|| src.matches(encoding, BINARY, false);
			U.failIf(!validEncoding, "Invalid Content-transfer-encoding header value!");
		}

		if (filename.isEmpty()) {
			int ind = data.add();
			data.keys[ind].assign(name);
			data.values[ind].assign(partBody);
		} else {
			int ind = files.add();
			files.keys[ind].assign(name);
			files.values[ind].assign(partBody);
		}
	}

	private boolean parseDisposition(Buf src, Range dispoA, Range dispoB, Range name, Range filename) {
		if (src.startsWith(dispoA, NAME_EQ, false)) {

			name.assign(dispoA);
			name.strip(NAME_EQ.length, 0);
			src.trim(name);
			name.strip(1, 1);

			if (src.startsWith(dispoB, FILENAME_EQ, false)) {
				filename.assign(dispoB);
				filename.strip(FILENAME_EQ.length, 0);
				src.trim(filename);
				filename.strip(1, 1);
			} else {
				filename.reset();
			}

			return true;
		}

		return false;
	}

	private boolean isMultipartForm(Buf buf, KeyValueRanges headers, Range multipartBoundary) {
		Range contType = headers.get(buf, CONTENT_TYPE, false);

		if (contType != null) {
			// TODO improve parsing of "multipart" and "data boundary"

			if (buf.startsWith(contType, MULTIPART_FORM_DATA_BOUNDARY1, false)) {
				multipartBoundary.setInterval(contType.start + MULTIPART_FORM_DATA_BOUNDARY1.length, contType.limit());
				return true;
			}

			if (buf.startsWith(contType, MULTIPART_FORM_DATA_BOUNDARY2, false)) {
				multipartBoundary.setInterval(contType.start + MULTIPART_FORM_DATA_BOUNDARY2.length, contType.limit());
				return true;
			}
		}

		multipartBoundary.reset();

		return false;
	}

}
