package org.rapidoid.commons;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class TimeSeries {

	private final NavigableMap<Long, Double> values = Collections.synchronizedNavigableMap(new TreeMap<Long, Double>());

	public void put(long timestamp, double value) {
		values.put(timestamp, value);
	}

	public NavigableMap<Long, Double> values() {
		return values;
	}

	@Override
	public String toString() {
		return U.frmt("TimeSerie(%s values)", values.size());
	}

}
