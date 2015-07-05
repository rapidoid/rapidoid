package org.rapidoidx.buffer;

/*
 * #%L
 * rapidoid-x-buffer
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import org.rapidoidx.data.Range;
import org.junit.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class RangeTest extends BufferTestCommons {

	@Test
	public void shouldBackupAndRestore() {
		Range range = new Range(10, 3);
		eq(range, 10, 3);

		long backup = range.backup();

		range.set(7, 17);
		eq(range, 7, 17);

		range.restore(backup);
		eq(range, 10, 3);
	}

	@Test
	public void statisticalTest() {
		Range rng = new Range();
		isTrue(rng.isEmpty());

		int[] borders = { Integer.MIN_VALUE, -1111, -1, 0, 1, 1111, Integer.MAX_VALUE };

		for (int i = 0; i < borders.length; i++) {
			for (int j = 0; j < borders.length; j++) {
				check(rng, borders[i], borders[j]);
			}
		}

		for (int i = 0; i < 1000000; i++) {
			check(rng, rnd(), rnd());
		}
	}

	private void check(Range rng, int a, int b) {
		rng.set(a, b);

		eq(rng, a, b);
		eq(rng.limit(), rng.start + rng.length);

		long backup = rng.backup();
		rng.set(rnd(), rnd());

		rng.restore(backup);
		eq(rng, a, b);
	}

}
