package org.rapidoid.util;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.io.IOException;
import java.io.OutputStream;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class StreamUtils extends RapidoidThing {

	private static final String MIN_VALUE = Long.MIN_VALUE + "";

	public static void putNumAsText(OutputStream out, long n) throws IOException {
		if (n >= 0) {
			writeDigits(out, n);
		} else {
			if (n == Long.MIN_VALUE) {
				writeAscii(out, MIN_VALUE);
			} else {
				out.write('-');
				putNumAsText(out, -n);
			}
		}
	}

	private static void writeDigits(OutputStream out, long n) throws IOException {
		int digit = (int) (n % 10);

		if (n >= 10) {
			writeDigits(out, n / 10);
		}

		out.write(digit + '0');
	}

	public static void writeAscii(OutputStream out, String s) throws IOException {
		for (int i = 0; i < s.length(); i++) {
			out.write(s.charAt(i));
		}
	}

}
