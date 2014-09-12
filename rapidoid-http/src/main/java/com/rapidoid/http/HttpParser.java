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
import org.rapidoid.net.RapidoidHelper;
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

		parsePathAndQuery(buf, uri, path, query);

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

	public void parsePathAndQuery(Buf buf, Range uri, Range path, Range query) {
		if (buf.split(uri, ASTERISK, path, query)) {
		} else {
			path.assign(uri);
			query.reset();
		}
	}

	public void parseHeaders(Buf buf, int from, int to, KeyValueRanges hdr) {
		int pos = buf.position();
		int limit = buf.limit();

		buf.position(from);
		buf.limit(to);

		int ind;
		do {
			ind = hdr.add();
			buf.scanUntil(CR, hdr.keys[ind], false);
			buf.position(buf.position() + 1);
		} while (!hdr.keys[ind].isEmpty());

		buf.position(pos);
		buf.limit(limit);
	}

	public void parseHeadersIntoKV(Buf buf, Ranges headers, KeyValueRanges headersKV) {
		for (int i = 0; i < headers.count; i++) {
			Range hdr = headers.ranges[i];
			int ind = headersKV.add();
			Range keys = headersKV.keys[ind];
			Range vals = headersKV.values[ind];

			buf.split(hdr, COL, keys, vals);
			buf.trim(keys);
			buf.trim(vals);
			if (keys.isEmpty() || vals.isEmpty()) {
				headersKV.count--;
			}
		}
	}

}
