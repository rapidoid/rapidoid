package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Dates extends RapidoidThing {

	protected static final Calendar CALENDAR = Calendar.getInstance();

	public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

	public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

	private static volatile long updateCurrDateAfter = 0;

	private static volatile byte[] CURR_DATE_BYTES;

	public static byte[] getDateTimeBytes() {
		long time = System.currentTimeMillis();

		// avoid synchronization for better performance
		if (time > updateCurrDateAfter) {

			// RFC 1123 date-time format, e.g. Sun, 07 Sep 2014 00:17:29 GMT
			DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ROOT);
			dateFormat.setTimeZone(GMT);

			Date date = new Date();
			date.setTime(time);

			CURR_DATE_BYTES = dateFormat.format(date).getBytes();
			updateCurrDateAfter = time + 1000;
		}

		return CURR_DATE_BYTES;
	}

	public static Date date(String value) {
		if (U.isEmpty(value)) {
			return null;
		}

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
		CALENDAR.set(year, month - 1, day, 0, 0, 0);
		return CALENDAR.getTime();
	}

	public static synchronized int thisYear() {
		CALENDAR.setTime(new Date());
		return CALENDAR.get(Calendar.YEAR);
	}

	public static String str(Date date) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		df.setTimeZone(UTC);
		return df.format(date);
	}

	public static String iso(Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		df.setTimeZone(UTC);
		return df.format(date);
	}

	public static String day() {
		return frmt("yyyy-MM-dd").format(U.time());
	}

	public static String hour() {
		return frmt("yyyy-MM-dd-HH").format(U.time());
	}

	public static String minute() {
		return frmt("yyyy-MM-dd-HH-mm").format(U.time());
	}

	public static String second() {
		return frmt("yyyy-MM-dd-HH-mm-ss").format(U.time());
	}

	public static SimpleDateFormat frmt(String frmt) {
		return new SimpleDateFormat(frmt);
	}

	public static String frmt(String frmt, Date date) {
		return frmt(frmt).format(date);
	}

	public static Calendar calendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public static String readable(Date date) {
		Calendar cal = Dates.calendar(date);

		if (cal.get(Calendar.YEAR) == Dates.thisYear()) {
			return frmt("dd MMM, HH:mm", date);
		} else {
			return frmt("dd MMM yyyy, HH:mm", date);
		}
	}

	public static String details(Date date) {
		return frmt("EEE, dd MMM yyyy HH:mm:ss z", date);
	}

}
