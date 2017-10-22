package org.rapidoid.datamodel.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.datamodel.PageableData;
import org.rapidoid.u.U;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.4.6")
public class NoResults<T> extends ResultsImpl<T> {

	public NoResults() {
		super((PageableData<T>) noData());
	}

	private static <T> PageableData<T> noData() {
		return new PageableData<T>() {

			@Override
			public List<T> getPage(long skip, long limit) {
				return U.list();
			}

			@Override
			public long getCount() {
				return 0;
			}
		};
	}

	@Override
	public String toString() {
		return "[N/A]";
	}
}
