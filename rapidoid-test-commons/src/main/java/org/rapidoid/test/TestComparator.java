/*-
 * #%L
 * rapidoid-test-commons
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

package org.rapidoid.test;

import org.junit.Assert;

import java.util.Objects;

/**
 * @author Nikolche Mihajlovski
 * @since 6.0.0
 */
public class TestComparator {

	private static final String OS = System.getProperty("os.name").toLowerCase();

	protected void check(String desc, String actual, String expected) {
		actual = platformNeutral(actual);
		expected = platformNeutral(expected);

		if (!Objects.equals(actual, expected)) {
			System.out.println("FAILURE: " + desc);
		}

		Assert.assertEquals(expected, actual);
	}

	public static String platformNeutral(String httpResponse) {
		if (OS.contains("win")) {

			// remove carriage returns to make tests platform independent
			httpResponse = httpResponse.replaceAll("(\\r)", "");

			// remove the content length line to make tests pass on any platform
			httpResponse = httpResponse.replaceAll("Content-Length:([0-9\\n ]+)", "");
		}

		return httpResponse;
	}

}
