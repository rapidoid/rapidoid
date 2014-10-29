package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Dates {

	protected static final Calendar CALENDAR = Calendar.getInstance();

	/* RFC 1123 date-time format, e.g. Sun, 07 Sep 2014 00:17:29 GMT */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	private static final Date CURR_DATE = new Date();
	private static byte[] CURR_DATE_BYTES;
	private static long updateCurrDateAfter = 0;

	static {
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static byte[] getDateTimeBytes() {
		long time = System.currentTimeMillis();

		// avoid synchronization for better performance
		if (time > updateCurrDateAfter) {
			CURR_DATE.setTime(time);
			CURR_DATE_BYTES = DATE_FORMAT.format(CURR_DATE).getBytes();
			updateCurrDateAfter = time + 1000;
		}

		return CURR_DATE_BYTES;
	}

	public static Date date(String value) {
		String[] parts = value.split("(\\.|-|/)");

		int a = parts.length > 0 ? U.num(parts[0]) : -1;
		int b = parts.length > 1 ? U.num(parts[1]) : -1;
		int c = parts.length > 2 ? U.num(parts[2]) : -1;

		switch (parts.length) {
		case 3:
			if (isDay(a) && isMonth(b) && isYear(c)) {
				return date(a, b, c);
			} else if (isYear(a) && isMonth(b) && isDay(c)) {
				return date(c, b, a);
			}
			break;
		case 2:
			if (isDay(a) && isMonth(b)) {
				return date(a, b, thisYear());
			}
			break;
		default:
		}

		throw U.rte("Invalid date: " + value);
	}

	private static boolean isDay(int day) {
		return day >= 1 && day <= 31;
	}

	private static boolean isMonth(int month) {
		return month >= 1 && month <= 12;
	}

	private static boolean isYear(int year) {
		return year >= 1000;
	}

	public static synchronized Date date(int day, int month, int year) {
		CALENDAR.set(year, month - 1, day - 1);
		return CALENDAR.getTime();
	}

	public static synchronized int thisYear() {
		CALENDAR.setTime(new Date());
		return CALENDAR.get(Calendar.YEAR);
	}

}
