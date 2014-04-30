package com.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import org.rapidoid.util.U;

public class HttpResponse {

	private static final String CONTENT_LENGTH = "Content-Length:";

	private static final String DATE = "Date:";

	private static final DateFormat FORMAT;

	static {
		FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private final byte[] bytes;

	final int contentLengthPos;

	final int datePos;

	private final Date date = new Date();

	private long updateAfter = 0;

	public HttpResponse(String resp) {
		this.bytes = resp.getBytes(U.UTF8);
		this.contentLengthPos = resp.indexOf(CONTENT_LENGTH) + CONTENT_LENGTH.length() + 1;
		this.datePos = resp.indexOf(DATE) + DATE.length() + 1;
	}

	public byte[] bytes() {
		long time = U.time();

		// allow race conditions for performance reasons
		if (time > updateAfter) {
			if (time > updateAfter) {
				date.setTime(time);
				byte[] dateBytes = FORMAT.format(date).getBytes();
				System.arraycopy(dateBytes, 0, bytes, datePos, dateBytes.length);
				updateAfter = time + 1000;
			}
		}

		return bytes;
	}

}
