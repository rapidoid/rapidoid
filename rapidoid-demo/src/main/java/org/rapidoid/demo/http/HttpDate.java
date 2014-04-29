package org.rapidoid.demo.http;

/*
 * #%L
 * rapidoid-demo
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HttpDate {

	private static final DateFormat FORMAT;
	private static final Date DATE = new Date();

	private static byte[] DATE_BYTES;

	private static long updateAfter = 0;

	static {
		FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static byte[] get() {
		long time = System.currentTimeMillis();

		if (time > updateAfter) {
			synchronized (DATE) {
				if (time > updateAfter) {
					DATE.setTime(time);
					DATE_BYTES = ("Date: " + FORMAT.format(DATE) + "\r\n").getBytes();
					updateAfter = time + 1000;
				}
			}
		}

		return DATE_BYTES;
	}

}
