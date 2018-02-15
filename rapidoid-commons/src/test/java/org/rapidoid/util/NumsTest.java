/*-
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.util;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Nums;
import org.rapidoid.test.TestCommons;

@Authors("Nikolche Mihajlovski")
@Since("5.6.0")
public class NumsTest extends TestCommons {

	@Test
	public void log2() {
		eq(Nums.log2(1), 0);
		eq(Nums.log2(2), 1);

		eq(Nums.log2(3), 2);
		eq(Nums.log2(4), 2);

		eq(Nums.log2(5), 3);
		eq(Nums.log2(8), 3);

		eq(Nums.log2(9), 4);
		eq(Nums.log2(16), 4);

		eq(Nums.log2(1024), 10);
		eq(Nums.log2(65536), 16);
		eq(Nums.log2(65536 * 1024), 26);

		eq(Nums.log2(Integer.MAX_VALUE), 31);
	}

	@Test
	public void percent() {
		eq(Nums.percent(0), 0.0);
		eq(Nums.percent(0.9), 90.0);

		eq(Nums.percent(0.12341), 12.34);
		eq(Nums.percent(0.12349), 12.35);
	}

}
