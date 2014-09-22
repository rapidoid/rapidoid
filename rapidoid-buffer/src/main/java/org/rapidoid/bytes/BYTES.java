package org.rapidoid.bytes;

/*
 * #%L
 * rapidoid-buffer
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

import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;

public class BYTES implements Constants {

	public static Bytes from(byte[] bytes) {
		return new ByteArrayBytes(bytes);
	}

	public static Bytes from(String s) {
		return new StringBytes(s);
	}

	public static int parseLines(Bytes bytes, Ranges lines, int start, int limit) {
		Range line;

		byte b0 = 0, b1 = 0, b2 = 0, b3 = 0;
		int i;
		for (i = start; i < limit; i++) {
			b0 = b1;
			b1 = b2;
			b2 = b3;
			b3 = bytes.get(i);

			if (b3 == LF) {
				int k = lines.add();
				line = lines.ranges[k];
				int pp = b2 == CR ? i : i - 1;
				line.set(0, pp);
				if (b0 == CR) {
					break;
				}
			}
		}
		
		return i;
	}

	public static void main(String[] args) {
		String req = "GET /asd/ff?a=5&bn=4 HTTP/1.1\r\nHost:www.test.com\r\nSet-Cookie: a=2\r\nConnection: keep-alive\r\n\r\n";

		final Ranges lines = new Ranges(111);
		final Bytes bytes = from(req);

		for (int i = 0; i < 50; i++) {
			U.benchmark("parse", 3000000, new Runnable() {
				@Override
				public void run() {
					lines.count = 0;
					parseLines(bytes, lines, 0, bytes.limit());
				}
			});
		}
	}

}
