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
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;

public class HttpParser implements Constants {

	private static final byte[] CONTENT_LENGTH = "Content-Length".getBytes();

	void parse(Buf buf, WebExchangeImpl req) {
		buf.scanUntil(SPACE, req.verb, true);

		int which = buf.scanTo(SPACE, ASTERISK, req.path, true);
		if (which == 2) {
			buf.scanUntil(SPACE, req.query, true);
		} else {
			req.query.reset();
		}

		buf.scanUntil(CR, req.protocol, true);
		buf.skip(1);

		KeyValueRanges hdr = req.headers;

		int ind = 0;

		while (buf.peek() != CR) {
			ind = hdr.add();

			buf.scanUntil(COL, hdr.keys[ind], true);

			buf.scanWhile(SPACE, Range.NONE, true);

			buf.scanUntil(CR, hdr.values[ind], true);
			buf.skip(1);

			buf.trim(hdr.keys[ind]);
			buf.trim(hdr.values[ind]);
		}

		buf.skip(2);

		// FIXME: check if GET verb
		Range clen = req.headers.get(buf, CONTENT_LENGTH, false);

		if (clen != null) {
			long len = buf.getN(clen);
			U.ensure(len >= 0 && len <= Integer.MAX_VALUE, "Invalid body size!");
			buf.scanN((int) len, req.body);
			// U.print("!!! body complete " + req.body);
		}
	}

	void parseHeaders(Buf buf, int from, int to, KeyValueRanges hdr) {
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

	void parseParams(Buf buf, KeyValueRanges params, Range range) {
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

}
