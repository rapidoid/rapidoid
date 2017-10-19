package org.rapidoid.writable;

/*
 * #%L
 * rapidoid-commons
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import static org.rapidoid.util.Constants.*;

@Authors("Nikolche Mihajlovski")
@Since("5.3.4")
public class WritableUtils extends RapidoidThing {

	private static final String MIN_VALUE = Long.MIN_VALUE + "";

	public static void putNumAsText(Writable out, long n) {
		if (n >= 0) {
			writeDigits(out, n);
		} else {
			if (n == Long.MIN_VALUE) {
				writeAscii(out, MIN_VALUE);
			} else {
				out.writeByte((byte) '-');
				putNumAsText(out, -n);
			}
		}
	}

	private static void writeDigits(Writable out, long n) {
		int digit = (int) (n % 10);

		if (n >= 10) {
			writeDigits(out, n / 10);
		}

		out.writeByte((byte) (digit + '0'));
	}

	public static void writeAscii(Writable out, String s) {
		for (int i = 0; i < s.length(); i++) {
			out.writeByte((byte) s.charAt(i));
		}
	}

	public static void writeAscii(Writable out, byte[] bytes) {
		out.writeBytes(bytes);
	}

	public static void writeUTF8(Writable out, String src) {
		int limit = src.length();

		for (int i = 0; i < limit; i++) {
			char c = src.charAt(i);

			if (c < 128) {
				out.writeByte((byte) c);

			} else {
				// https://en.wikipedia.org/wiki/UTF-8

				if (c < 0x800) {
					// [110xxxxx, 10xxxxxx]
					out.writeByte((byte) (UTF8_2_BYTES_LEAD | c >> 6));     // highest 5 bits
					out.writeByte((byte) (UTF8_CONTINUATION | c & LAST_6)); // lowest 6 bits

				} else if (Character.isSurrogate(c)) {

					if (Character.isHighSurrogate(c)) {
						int next = i + 1;

						if (next < limit) {
							char nextChar = src.charAt(next);

							if (Character.isLowSurrogate(nextChar)) {
								int cp = Character.toCodePoint(c, nextChar);

								// [11110xxx, 10xxxxxx, 10xxxxxx, 10xxxxxx]
								out.writeByte((byte) (UTF8_4_BYTES_LEAD | cp >> 18));          // highest 3 bits
								out.writeByte((byte) (UTF8_CONTINUATION | cp >> 12 & LAST_6)); // next 6 bits
								out.writeByte((byte) (UTF8_CONTINUATION | cp >> 6 & LAST_6));  // next 6 bits
								out.writeByte((byte) (UTF8_CONTINUATION | cp & LAST_6));       // lowest 6 bits

								i++; // the next char was successfully consumed

							} else {
								out.writeByte(MALFORMED_CHAR); // expected low surrogate
							}

						} else {
							out.writeByte(MALFORMED_CHAR); // expected one more character
						}

					} else {
						out.writeByte(MALFORMED_CHAR); // expected high surrogate
					}

				} else {
					// [1110xxxx, 10xxxxxx, 10xxxxxx]
					out.writeByte((byte) (UTF8_3_BYTES_LEAD | c >> 12));         // highest 4 bits
					out.writeByte((byte) (UTF8_CONTINUATION | c >> 6 & LAST_6)); // next 6 bits
					out.writeByte((byte) (UTF8_CONTINUATION | c & LAST_6));      // lowest 6 bits
				}
			}
		}
	}

	public static void writeUTF8HtmlEscaped(Writable out, String src) {
		int limit = src.length();

		for (int i = 0; i < limit; i++) {
			char c = src.charAt(i);

			if (c < 128) {

				switch (c) {
					case '<': // &lt;
						out.writeByte((byte) '&');
						out.writeByte((byte) 'l');
						out.writeByte((byte) 't');
						out.writeByte((byte) ';');
						break;

					case '>': // &gt;
						out.writeByte((byte) '&');
						out.writeByte((byte) 'g');
						out.writeByte((byte) 't');
						out.writeByte((byte) ';');
						break;

					case '&': // &amp;
						out.writeByte((byte) '&');
						out.writeByte((byte) 'a');
						out.writeByte((byte) 'm');
						out.writeByte((byte) 'p');
						out.writeByte((byte) ';');
						break;

					case '"': // &quot;
						out.writeByte((byte) '&');
						out.writeByte((byte) 'q');
						out.writeByte((byte) 'u');
						out.writeByte((byte) 'o');
						out.writeByte((byte) 't');
						out.writeByte((byte) ';');
						break;

					case '\'': // &apos;
						out.writeByte((byte) '&');
						out.writeByte((byte) 'a');
						out.writeByte((byte) 'p');
						out.writeByte((byte) 'o');
						out.writeByte((byte) 's');
						out.writeByte((byte) ';');
						break;

					default:
						out.writeByte((byte) c);
						break;
				}

			} else {
				// https://en.wikipedia.org/wiki/UTF-8

				if (c < 0x800) {
					// [110xxxxx, 10xxxxxx]
					out.writeByte((byte) (UTF8_2_BYTES_LEAD | c >> 6));     // highest 5 bits
					out.writeByte((byte) (UTF8_CONTINUATION | c & LAST_6)); // lowest 6 bits

				} else if (Character.isSurrogate(c)) {

					if (Character.isHighSurrogate(c)) {
						int next = i + 1;

						if (next < limit) {
							char nextChar = src.charAt(next);

							if (Character.isLowSurrogate(nextChar)) {
								int cp = Character.toCodePoint(c, nextChar);

								// [11110xxx, 10xxxxxx, 10xxxxxx, 10xxxxxx]
								out.writeByte((byte) (UTF8_4_BYTES_LEAD | cp >> 18));          // highest 3 bits
								out.writeByte((byte) (UTF8_CONTINUATION | cp >> 12 & LAST_6)); // next 6 bits
								out.writeByte((byte) (UTF8_CONTINUATION | cp >> 6 & LAST_6));  // next 6 bits
								out.writeByte((byte) (UTF8_CONTINUATION | cp & LAST_6));       // lowest 6 bits

								i++; // the next char was successfully consumed

							} else {
								out.writeByte(MALFORMED_CHAR); // expected low surrogate
							}

						} else {
							out.writeByte(MALFORMED_CHAR); // expected one more character
						}

					} else {
						out.writeByte(MALFORMED_CHAR); // expected high surrogate
					}

				} else {
					// [1110xxxx, 10xxxxxx, 10xxxxxx]
					out.writeByte((byte) (UTF8_3_BYTES_LEAD | c >> 12));         // highest 4 bits
					out.writeByte((byte) (UTF8_CONTINUATION | c >> 6 & LAST_6)); // next 6 bits
					out.writeByte((byte) (UTF8_CONTINUATION | c & LAST_6));      // lowest 6 bits
				}
			}
		}
	}

}
