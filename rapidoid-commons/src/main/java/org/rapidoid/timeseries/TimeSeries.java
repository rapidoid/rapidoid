package org.rapidoid.timeseries;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Stats;
import org.rapidoid.u.U;
import org.rapidoid.util.SlidingWindowList;

import java.util.*;

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
@Since("5.1.0")
public class TimeSeries extends RapidoidThing {

	private static final int OVERVIEW_SIZE_THRESHOLD = 120;

	private static final long MILLIS_IN_MINUTE = 60 * 1000;
	private static final long MILLIS_IN_HOUR = 60 * MILLIS_IN_MINUTE;
	private static final long MILLIS_IN_DAY = 24 * MILLIS_IN_HOUR;
	private static final long MILLIS_IN_MONTH = 28 * MILLIS_IN_DAY; // simplified as 4 weeks

	private final List<TSValue> values;

	private final Stats stats = new Stats();

	private final Map<Long, Stats> monthly = Coll.autoExpandingMap(Long.class, Stats.class);

	private final Map<Long, Stats> daily = Coll.autoExpandingMap(Long.class, Stats.class);

	private final Map<Long, Stats> hourly = Coll.autoExpandingMap(Long.class, Stats.class);

	private final Map<Long, Stats> minutely = Coll.autoExpandingMap(Long.class, Stats.class);

	private final Map<Long, Stats> perTenSeconds = Coll.autoExpandingMap(Long.class, Stats.class);

	private volatile String title;

	public TimeSeries() {
		this(7 * 24 * 3600);
	}

	public TimeSeries(int maxSize) {
		this.values = new SlidingWindowList<>(maxSize);
	}

	public void put(long timestamp, double value) {

		synchronized (this) {
			TSValue ts = new TSValue(timestamp, value);

			int pos = Collections.binarySearch(values, ts);
			if (pos < 0) pos = ~pos;

			values.add(pos, ts);
		}

		stats.add(value);

		long month = month(timestamp);
		long day = day(timestamp);
		long hour = hour(timestamp);
		long minute = minute(timestamp);

		monthly.get(month).add(value);
		daily.get(day).add(value);
		hourly.get(hour).add(value);
		minutely.get(minute).add(value);
	}

	public NavigableMap<Long, Double> values() {
		return null;
	}

	@Override
	public String toString() {
		return U.frmt("TimeSerie(%s)", stats);
	}

	public NavigableMap<Long, Double> overview() {
		synchronized (this) {
			if (!values.isEmpty()) {
				return overviewOf(U.first(values).timestamp, U.last(values).timestamp);
			} else {
				return new TreeMap<>();
			}
		}
	}

	public NavigableMap<Long, Double> overview(long from, long to) {
		return overviewOf(from, to);
	}

	private NavigableMap<Long, Double> overviewOf(long from, long to) {

		NavigableMap<Long, Double> overview = new TreeMap<Long, Double>();

		long diff = to - from;
		U.must(diff >= 0);

		synchronized (this) {
			if (values.size() <= OVERVIEW_SIZE_THRESHOLD) {
				putAll(overview, values);
				return overview;
			}
		}

		double diffMinutes = ((double) diff) / MILLIS_IN_MINUTE;
		double diffHours = diffMinutes / 60;
		double diffDays = diffHours / 24;

		if (diffDays > 180) { // more than 6 months => monthly
			long fromMonth = month(from);
			long toMonth = month(to);

			for (long month = fromMonth; month <= toMonth; month++) {
				double avg = monthly.get(month).avg();
				overview.put(month * MILLIS_IN_MONTH, avg);
			}

			return overview;
		}

		if (diffDays > 15) { // 15 - 180 days -> daily
			long fromDay = day(from);
			long toDay = day(to);

			for (long day = fromDay; day <= toDay; day++) {
				double avg = daily.get(day).avg();
				overview.put(day * MILLIS_IN_DAY, avg);
			}

			return overview;
		}

		if (diffDays > 0.25) { // 6 hours - 14 days -> hourly
			long fromHour = hour(from);
			long toHour = hour(to);

			for (long hour = fromHour; hour <= toHour; hour++) {
				double avg = hourly.get(hour).avg();
				overview.put(hour * MILLIS_IN_HOUR, avg);
			}

			return overview;
		}

		if (diffMinutes > 30) { // more than 30 minutes -> minutely

			long fromMinute = minute(from);
			long toMinute = minute(to);

			for (long minute = fromMinute; minute <= toMinute; minute++) {
				double avg = minutely.get(minute).avg();
				overview.put(minute * MILLIS_IN_MINUTE, avg);
			}

			return overview;
		}

		// less than 30 minutes
		synchronized (this) {

			int pos1 = Collections.binarySearch(values, new TSValue(from, 0));
			if (pos1 < 0) pos1 = ~pos1;

			int pos2 = Collections.binarySearch(values, new TSValue(to, 0));
			if (pos2 < 0) pos2 = ~pos2;

			List<TSValue> sub = pos1 <= pos2 ? values.subList(pos1, pos2) : values.subList(pos2, pos1);
			putAll(overview, sub);
		}

		return overview;
	}

	private void putAll(Map<Long, Double> dest, List<TSValue> src) {
		for (TSValue ts : src) {
			dest.put(ts.timestamp, ts.value);
		}
	}

	private static long month(long timestamp) {
		return timestamp / MILLIS_IN_MONTH;
	}

	private static long day(long timestamp) {
		return timestamp / MILLIS_IN_DAY;
	}

	private static long hour(long timestamp) {
		return timestamp / MILLIS_IN_HOUR;
	}

	private static long minute(long timestamp) {
		return timestamp / MILLIS_IN_MINUTE;
	}

	public TimeSeries title(String title) {
		this.title = title;
		return this;
	}

	public String title() {
		return title;
	}
}
