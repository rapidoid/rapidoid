package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Err;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.BufRanges;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.http.HttpContentType;
import org.rapidoid.io.Upload;
import org.rapidoid.log.Log;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.u.U;
import org.rapidoid.wrap.IntWrap;

import java.util.List;
import java.util.Map;

import static org.rapidoid.util.Constants.*;

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
@Since("2.0.0")
public class HttpParser extends RapidoidThing {

	private static final byte[] CONNECTION = "Connection:".getBytes();

	private static final byte[] KEEP_ALIVE = "keep-alive".getBytes();

	private static final byte[] CONTENT_LENGTH = "Content-Length:".getBytes();

	private static final byte[] COOKIE = "Cookie".getBytes();

	private static final byte[] CT_MULTIPART_FORM_DATA_BOUNDARY1 = "multipart/form-data; boundary=".getBytes();

	private static final byte[] CT_MULTIPART_FORM_DATA_BOUNDARY2 = "multipart/form-data;boundary=".getBytes();

	private static final byte[] CT_MULTIPART_FORM_DATA = "multipart/form-data".getBytes();

	private static final byte[] CT_FORM_URLENCODED = "application/x-www-form-urlencoded".getBytes();

	private static final byte[] CT_JSON = "application/json".getBytes();

	private static final byte[] CONTENT_TYPE = "Content-Type".getBytes();

	private static final byte[] CONTENT_DISPOSITION = "Content-Disposition".getBytes();

	private static final byte[] FORM_DATA = "form-data;".getBytes();

	private static final byte[] NAME_EQ = "name=".getBytes();

	private static final byte[] FILENAME_EQ = "filename=".getBytes();

	private static final byte[] CHARSET_EQ = "charset=".getBytes();

	private static final byte[] _UTF_8 = "UTF-8".getBytes();

	private static final byte[] _ISO_8859_1 = "ISO-8859-1".getBytes();

	private static final byte[] CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding".getBytes();

	private static final byte[] _7BIT = "7bit".getBytes();

	private static final byte[] _8BIT = "8bit".getBytes();

	private static final byte[] BINARY = "binary".getBytes();

	private static final byte[] GET = "GET".getBytes();

	public void parse(Buf buf, RapidoidHelper helper) {

		Bytes bytes = buf.bytes();

		BufRange protocol = helper.protocol;
		BufRanges headers = helper.headers;

		buf.scanUntil(SPACE, helper.verb);
		buf.scanUntil(SPACE, helper.uri);
		buf.scanLn(protocol);

		helper.isKeepAlive.value = detectKeepAlive(buf, helper, bytes, protocol, headers);

		BytesUtil.split(bytes, helper.uri, ASTERISK, helper.path, helper.query, false);

		helper.isGet.value = BytesUtil.matches(bytes, helper.verb, GET, true);
		if (!helper.isGet.value) {
			parseBody(buf, helper);
		}
	}

	private boolean detectKeepAlive(Buf buf, RapidoidHelper helper, Bytes bytes, BufRange protocol, BufRanges headers) {
		IntWrap result = helper.integers[0];
		boolean keepAliveByDefault = protocol.isEmpty() || bytes.get(protocol.last()) != '0'; // e.g. HTTP/1.1

		// try to detect the opposite of the default
		if (keepAliveByDefault) {
			buf.scanLnLn(headers.reset(), result, (byte) 's', (byte) 'e'); // clo[se]

		} else {
			buf.scanLnLn(headers.reset(), result, (byte) 'v', (byte) 'e'); // keep-ali[ve]
		}

		int possibleConnHeaderPos = result.value;

		if (possibleConnHeaderPos < 0) return keepAliveByDefault; // no evidence of the opposite

		BufRange possibleConnHdr = headers.get(possibleConnHeaderPos);
		if (BytesUtil.startsWith(bytes, possibleConnHdr, CONNECTION, true)) {
			return !keepAliveByDefault; // detected the opposite of the default
		}

		return isKeepAlive(bytes, headers, helper, keepAliveByDefault);
	}

	private boolean isKeepAlive(Bytes bytes, BufRanges headers, RapidoidHelper helper, boolean keepAliveByDefault) {
		BufRange connHdr = headers.getByPrefix(bytes, CONNECTION, false);

		return connHdr != null ? getKeepAliveValue(bytes, connHdr, helper) : keepAliveByDefault;
	}

	private boolean getKeepAliveValue(Bytes bytes, BufRange connHdr, RapidoidHelper helper) {
		assert bytes != null;
		assert connHdr != null;

		BufRange connVal = helper.ranges5.ranges[3];

		connVal.setInterval(connHdr.start + CONNECTION.length, connHdr.limit());
		BytesUtil.trim(bytes, connVal);

		return BytesUtil.matches(bytes, connVal, KEEP_ALIVE, false);
	}

	private void parseBody(Buf buf, RapidoidHelper helper) {
		BufRanges headers = helper.headers;
		BufRange body = helper.body;

		BufRange clen = headers.getByPrefix(buf.bytes(), CONTENT_LENGTH, false);

		if (clen != null) {
			BufRange clenValue = helper.ranges5.ranges[helper.ranges5.ranges.length - 1];
			clenValue.setInterval(clen.start + CONTENT_LENGTH.length, clen.limit());
			BytesUtil.trim(buf.bytes(), clenValue);
			long len = buf.getN(clenValue);
			U.must(len >= 0 && len <= Integer.MAX_VALUE, "Invalid body size!");
			buf.scanN((int) len, body);
			Log.debug("Request body complete", "range", body);
		} else {
			body.reset();
		}
	}

	public void parseParams(Buf buf, KeyValueRanges params, BufRange range) {
		parseURLEncodedKV(buf, params, range);
	}

	private void parseURLEncodedKV(Buf buf, KeyValueRanges params, BufRange body) {
		int pos = buf.position();
		int limit = buf.limit();

		buf.position(body.start);
		buf.limit(body.limit());

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

		BufRanges headers = helper.ranges2.reset();
		buf.scanLnLn(headers);

		parseHeadersIntoKV(buf, headers, headersKV, null, helper);

		int bodyPos = buf.position();

		buf.position(pos);
		buf.limit(limit);

		return bodyPos;
	}

	public void parseHeadersIntoKV(Buf buf, BufRanges headers, KeyValueRanges headersKV, KeyValueRanges cookies,
	                               RapidoidHelper helper) {

		BufRange cookie = helper.ranges5.ranges[0];

		for (int i = 0; i < headers.count; i++) {
			BufRange hdr = headers.ranges[i];
			int ind = headersKV.add();
			BufRange key = headersKV.keys[ind];
			BufRange val = headersKV.values[ind];

			assert !hdr.isEmpty();

			boolean split = BytesUtil.split(buf.bytes(), hdr, COL, key, val, true);
			U.must(split, "Invalid HTTP header!");

			if (cookies != null && BytesUtil.matches(buf.bytes(), key, COOKIE, false)) {
				headersKV.count--; // don't include cookies in headers

				do {
					BytesUtil.split(buf.bytes(), val, SEMI_COL, cookie, val, true);
					int cind = cookies.add();
					BytesUtil.split(buf.bytes(), cookie, EQ, cookies.keys[cind], cookies.values[cind], true);
				} while (!val.isEmpty());
			}
		}
	}

	/**
	 * @return <code>false</code> if JSON data was posted, so it wasn't completely parsed.
	 */
	public boolean parseBody(Buf src, KeyValueRanges headers, BufRange body, KeyValueRanges data,
	                         Map<String, List<Upload>> files, RapidoidHelper helper) {

		if (body.isEmpty()) {
			return true;
		}

		BufRange multipartBoundary = helper.ranges5.ranges[0];

		HttpContentType contentType = getContentType(src, headers, multipartBoundary);

		switch (contentType) {

			case MULTIPART:
				if (multipartBoundary.isEmpty()) {
					detectMultipartBoundary(src, body, multipartBoundary);
				}

				helper.bytes[0] = '-';
				helper.bytes[1] = '-';

				src.get(multipartBoundary, helper.bytes, 2);

				Err.rteIf(multipartBoundary.isEmpty(), "Invalid multi-part HTTP request!");

				Map<String, List<Upload>> autoFiles = Coll.mapOfLists();
				parseMultiParts(src, body, data, autoFiles, multipartBoundary, helper);
				files.putAll(autoFiles);

				return true;

			case FORM_URLENCODED:
				byte bodyStart = src.get(body.start);
				if (bodyStart != '{' && bodyStart != '[') {
					parseURLEncodedKV(src, data, body);
					return true;
				} else {
					return false;
				}

			case JSON:
				return false;

			case OTHER:
				return true;

			case NOT_FOUND:
				// fall back to json and try parsing the body
				return src.get(body.start) != '{';

			default:
				throw Err.notExpected();
		}
	}

	private void detectMultipartBoundary(Buf src, BufRange body, BufRange multipartBoundary) {
		BytesUtil.parseLine(src.bytes(), multipartBoundary, body.start, body.limit());
		multipartBoundary.strip(2, 0);
	}

	/* http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2 */
	private void parseMultiParts(Buf src, BufRange body, KeyValueRanges data, Map<String, List<Upload>> files,
	                             BufRange multipartBoundary, RapidoidHelper helper) {

		int start = body.start;
		int limit = body.limit();

		int sepLen = multipartBoundary.length + 2;
		int pos1 = -1, pos2;

		try {

			while ((pos2 = BytesUtil.find(src.bytes(), start, limit, helper.bytes, 0, sepLen, true)) >= 0) {
				if (pos1 >= 0 && pos2 >= 0) {
					int from = pos1 + sepLen + 2;
					int to = pos2 - 2;
					parseMultiPart(src, body, data, files, multipartBoundary, helper, from, to);
				}

				pos1 = pos2;
				start = pos2 + sepLen;
			}

		} catch (Throwable e) {
			Log.warn("Multipart parse error!", e);
			throw U.rte("Multipart data parse error!", e);
		}
	}

	private void parseMultiPart(Buf src, BufRange body, KeyValueRanges data, Map<String, List<Upload>> files,
	                            BufRange multipartBoundary, RapidoidHelper helper, int from, int to) {

		KeyValueRanges headers = helper.headersKV.reset();
		BufRange partBody = helper.ranges4.ranges[0];
		BufRange contType = helper.ranges4.ranges[1];
		BufRange contEnc = helper.ranges4.ranges[2];
		BufRange dispo1 = helper.ranges4.ranges[3];
		BufRange dispo2 = helper.ranges4.ranges[4];
		BufRange name = helper.ranges4.ranges[5];
		BufRange filename = helper.ranges4.ranges[6];
		BufRange charset = helper.ranges4.ranges[7];

		int bodyPos = parseHeaders(src, from, to, headers, helper);
		partBody.setInterval(bodyPos, to);

		// form-data; name="a" | form-data; name="f2"; filename="test2.txt"
		BufRange disposition = headers.get(src, CONTENT_DISPOSITION, false);

		if (BytesUtil.startsWith(src.bytes(), disposition, FORM_DATA, false)) {
			disposition.strip(FORM_DATA.length, 0);
		} else {
			return;
		}

		BytesUtil.split(src.bytes(), disposition, SEMI_COL, dispo1, dispo2, true);

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
		BufRange contentType = headers.get(src, CONTENT_TYPE, false);

		charset.reset();
		contType.reset();
		contEnc.reset();

		if (contentType != null) {
			BytesUtil.split(src.bytes(), contentType, SEMI_COL, contType, contEnc, true);
			if (BytesUtil.startsWith(src.bytes(), contEnc, CHARSET_EQ, false)) {
				charset.assign(contEnc);
				charset.strip(CHARSET_EQ.length, 0);
				BytesUtil.trim(src.bytes(), charset);

				if (!BytesUtil.matches(src.bytes(), charset, _UTF_8, false)
					&& !BytesUtil.matches(src.bytes(), charset, _ISO_8859_1, false)) {
					Log.warn("Tipically the UTF-8 and ISO-8859-1 charsets are expected, but received different!",
						"charset", src.get(charset));
				}
			}
		}

		// (OPTIONAL) e.g. 7bit | 8bit | binary | DEFAULT=7bit
		BufRange encoding = headers.get(src, CONTENT_TRANSFER_ENCODING, false);

		if (encoding != null) {
			boolean validEncoding = BytesUtil.matches(src.bytes(), encoding, _7BIT, false)
				|| BytesUtil.matches(src.bytes(), encoding, _8BIT, false)
				|| BytesUtil.matches(src.bytes(), encoding, BINARY, false);
			Err.rteIf(!validEncoding, "Invalid Content-transfer-encoding header value!");
		}

		if (filename.isEmpty()) {
			int ind = data.add();
			data.keys[ind].assign(name);
			data.values[ind].assign(partBody);
		} else {
			String uploadParamName = src.get(name);
			String uploadFilename = src.get(filename);
			byte[] uploadContent = partBody.bytes(src);
			files.get(uploadParamName).add(new Upload(uploadFilename, uploadContent));
		}
	}

	private boolean parseDisposition(Buf src, BufRange dispoA, BufRange dispoB, BufRange name, BufRange filename) {
		if (BytesUtil.startsWith(src.bytes(), dispoA, NAME_EQ, false)) {

			name.assign(dispoA);
			name.strip(NAME_EQ.length, 0);
			BytesUtil.trim(src.bytes(), name);
			name.strip(1, 1);

			if (BytesUtil.startsWith(src.bytes(), dispoB, FILENAME_EQ, false)) {
				filename.assign(dispoB);
				filename.strip(FILENAME_EQ.length, 0);
				BytesUtil.trim(src.bytes(), filename);
				filename.strip(1, 1);
			} else {
				filename.reset();
			}

			return true;
		}

		return false;
	}

	private HttpContentType getContentType(Buf buf, KeyValueRanges headers, BufRange multipartBoundary) {
		BufRange contType = headers.get(buf, CONTENT_TYPE, false);

		if (contType != null) {

			if (BytesUtil.startsWith(buf.bytes(), contType, CT_FORM_URLENCODED, false)) {
				multipartBoundary.reset();
				return HttpContentType.FORM_URLENCODED;
			}

			if (BytesUtil.startsWith(buf.bytes(), contType, CT_JSON, false)) {
				multipartBoundary.reset();
				return HttpContentType.JSON;
			}

			if (BytesUtil.startsWith(buf.bytes(), contType, CT_MULTIPART_FORM_DATA_BOUNDARY1, false)) {
				multipartBoundary.setInterval(contType.start + CT_MULTIPART_FORM_DATA_BOUNDARY1.length,
					contType.limit());
				return HttpContentType.MULTIPART;
			}

			if (BytesUtil.startsWith(buf.bytes(), contType, CT_MULTIPART_FORM_DATA_BOUNDARY2, false)) {
				multipartBoundary.setInterval(contType.start + CT_MULTIPART_FORM_DATA_BOUNDARY2.length,
					contType.limit());
				return HttpContentType.MULTIPART;
			}

			if (BytesUtil.startsWith(buf.bytes(), contType, CT_MULTIPART_FORM_DATA, false)) {
				multipartBoundary.reset();
				return HttpContentType.MULTIPART;
			}
		}

		multipartBoundary.reset();

		return contType != null ? HttpContentType.OTHER : HttpContentType.NOT_FOUND;
	}

	@SuppressWarnings("unchecked")
	public boolean parsePosted(Buf input, KeyValueRanges headersKV, BufRange rBody, KeyValueRanges posted,
	                           Map<String, List<Upload>> files, RapidoidHelper helper, Map<String, Object> dest) {

		boolean completed = parseBody(input, headersKV, rBody, posted, files, helper);

		posted.toUrlEncodedParams(input, dest);

		return completed;
	}

}
