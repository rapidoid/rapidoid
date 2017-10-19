package org.rapidoid.buffer;

/*
 * #%L
 * rapidoid-buffer
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class StatisticalBufTest extends BufferTestCommons {

	@Test
	public void shouldExpandAndShrink() {
		BufGroup bufs = new BufGroup(4);
		Buf buf = bufs.newBuf("");
		String copy = "";
		String s;

		for (int i = 0; i < 1000; i++) {
			if (rnd(3) > 0 || copy.isEmpty()) {
				s = rndStr(0, 9);
				buf.append(s);
				copy += s;
			} else {
				int len = rnd(Math.min(17, copy.length() + 1));

				switch (rnd(3)) {
					case 0:
						copy = copy.substring(len);
						buf.deleteBefore(len);
						break;

					case 1:
						copy = copy.substring(0, copy.length() - len);
						buf.deleteLast(len);
						break;

					case 2:
						s = rndStr(0, len);
						int maxPos = copy.length() - s.length();
						assert maxPos >= 0;
						int pos = rnd(maxPos + 1); // range [0..maxPos]

						copy = copy.substring(0, pos) + s + copy.substring(pos + s.length());
						buf.put(pos, s.getBytes(), 0, s.length());
						break;

					default:
						throw Err.notExpected();
				}
			}

			if (rnd(1000) == 0) {
				copy = "";
				buf.clear();
			}

			eq(buf, copy);
		}
	}

}
