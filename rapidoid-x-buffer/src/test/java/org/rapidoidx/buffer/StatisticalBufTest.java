package org.rapidoidx.buffer;

/*
 * #%L
 * rapidoid-x-buffer
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class StatisticalBufTest extends BufferTestCommons {

	@Test
	public void shouldExpandAndShrink() {
		BufGroup bufs = new BufGroup(2);
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
					throw U.notExpected();
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
