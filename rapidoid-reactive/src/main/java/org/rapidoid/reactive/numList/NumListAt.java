package org.rapidoid.reactive.numList;

import java.util.List;

import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.num.AbstractNum;

public class NumListAt extends AbstractNum {

	private final List<Num> list;
	private final Num index;

	public NumListAt(List<Num> list, Num index) {
		this.list = list;
		this.index = index;
	}

	@Override
	public int get() {
		return list.get(index.get()).get();
	}

}
