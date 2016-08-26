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
public class StreamUtils extends RapidoidThing implements Constants {

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

	public static void writeUTF8(OutputStream out, String src) throws IOException {
		int limit = src.length();

		for (int i = 0; i < limit; i++) {
			char c = src.charAt(i);

			if (c < 128) {
				out.write(c);

			} else {
				// https://en.wikipedia.org/wiki/UTF-8

				if (c < 0x800) {
					// [110xxxxx, 10xxxxxx]
					out.write(UTF8_2_BYTES_LEAD | c >> 6);     // highest 5 bits
					out.write(UTF8_CONTINUATION | c & LAST_6); // lowest 6 bits

				} else if (Character.isSurrogate(c)) {

					if (Character.isHighSurrogate(c)) {
						int next = i + 1;

						if (next < limit) {
							char nextChar = src.charAt(next);

							if (Character.isLowSurrogate(nextChar)) {
								int cp = Character.toCodePoint(c, nextChar);

								// [11110xxx, 10xxxxxx, 10xxxxxx, 10xxxxxx]
								out.write(UTF8_4_BYTES_LEAD | cp >> 18);          // highest 3 bits
								out.write(UTF8_CONTINUATION | cp >> 12 & LAST_6); // next 6 bits
								out.write(UTF8_CONTINUATION | cp >> 6 & LAST_6);  // next 6 bits
								out.write(UTF8_CONTINUATION | cp & LAST_6);       // lowest 6 bits

								i++; // the next char was successfully consumed

							} else {
								out.write(MALFORMED_CHAR); // expected low surrogate
							}

						} else {
							out.write(MALFORMED_CHAR); // expected one more character
						}

					} else {
						out.write(MALFORMED_CHAR); // expected high surrogate
					}

				} else {
					// [1110xxxx, 10xxxxxx, 10xxxxxx]
					out.write(UTF8_3_BYTES_LEAD | c >> 12);         // highest 4 bits
					out.write(UTF8_CONTINUATION | c >> 6 & LAST_6); // next 6 bits
					out.write(UTF8_CONTINUATION | c & LAST_6);      // lowest 6 bits
				}
			}
		}
	}

	public static void writeUTF8HtmlEscaped(OutputStream out, String src) throws IOException {
		int limit = src.length();

		for (int i = 0; i < limit; i++) {
			char c = src.charAt(i);

			if (c < 128) {

				switch (c) {
					case '<': // &lt;
						out.write('&');
						out.write('l');
						out.write('t');
						out.write(';');
						break;

					case '>': // &gt;
						out.write('&');
						out.write('g');
						out.write('t');
						out.write(';');
						break;

					case '&': // &amp;
						out.write('&');
						out.write('a');
						out.write('m');
						out.write('p');
						out.write(';');
						break;

					case '"': // &quot;
						out.write('&');
						out.write('q');
						out.write('u');
						out.write('o');
						out.write('t');
						out.write(';');
						break;

					case '\'': // &apos;
						out.write('&');
						out.write('a');
						out.write('p');
						out.write('o');
						out.write('s');
						out.write(';');
						break;

					default:
						out.write(c);
						break;
				}

			} else {
				// https://en.wikipedia.org/wiki/UTF-8

				if (c < 0x800) {
					// [110xxxxx, 10xxxxxx]
					out.write(UTF8_2_BYTES_LEAD | c >> 6);     // highest 5 bits
					out.write(UTF8_CONTINUATION | c & LAST_6); // lowest 6 bits

				} else if (Character.isSurrogate(c)) {

					if (Character.isHighSurrogate(c)) {
						int next = i + 1;

						if (next < limit) {
							char nextChar = src.charAt(next);

							if (Character.isLowSurrogate(nextChar)) {
								int cp = Character.toCodePoint(c, nextChar);

								// [11110xxx, 10xxxxxx, 10xxxxxx, 10xxxxxx]
								out.write(UTF8_4_BYTES_LEAD | cp >> 18);          // highest 3 bits
								out.write(UTF8_CONTINUATION | cp >> 12 & LAST_6); // next 6 bits
								out.write(UTF8_CONTINUATION | cp >> 6 & LAST_6);  // next 6 bits
								out.write(UTF8_CONTINUATION | cp & LAST_6);       // lowest 6 bits

								i++; // the next char was successfully consumed

							} else {
								out.write(MALFORMED_CHAR); // expected low surrogate
							}

						} else {
							out.write(MALFORMED_CHAR); // expected one more character
						}

					} else {
						out.write(MALFORMED_CHAR); // expected high surrogate
					}

				} else {
					// [1110xxxx, 10xxxxxx, 10xxxxxx]
					out.write(UTF8_3_BYTES_LEAD | c >> 12);         // highest 4 bits
					out.write(UTF8_CONTINUATION | c >> 6 & LAST_6); // next 6 bits
					out.write(UTF8_CONTINUATION | c & LAST_6);      // lowest 6 bits
				}
			}
		}
	}

}
