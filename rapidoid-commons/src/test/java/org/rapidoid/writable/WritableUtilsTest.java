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

package org.rapidoid.writable;

import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.test.TestCommons;
import org.rapidoid.test.TestRnd;

import java.io.ByteArrayOutputStream;

@Authors("Nikolche Mihajlovski")
@Since("5.3.4")
public class WritableUtilsTest extends TestCommons {

	@Test
	public void testUri() {
		check(123, 2, 34);
		check(0, -1, -44);
		check(Long.MAX_VALUE, 100023, Long.MIN_VALUE + 1);
		check(12345, -125521209, Long.MIN_VALUE);

		for (int i = 0; i < 1000000; i++) {
			check(TestRnd.rndL(), TestRnd.rndL(), TestRnd.rndL());
		}
	}

	private void check(long n1, long n2, long n3) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		WritableOutputStream out = new WritableOutputStream(baos);

		WritableUtils.putNumAsText(out, n1);
		baos.write(',');
		WritableUtils.putNumAsText(out, n2);
		baos.write(':');
		WritableUtils.putNumAsText(out, n3);

		String s = new String(baos.toByteArray());
		eq(s, n1 + "," + n2 + ":" + n3);
	}

}
