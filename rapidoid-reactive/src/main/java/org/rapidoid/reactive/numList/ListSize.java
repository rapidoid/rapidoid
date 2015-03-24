package org.rapidoid.reactive.numList;

import java.util.List;

import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.num.AbstractNum;

public class ListSize extends AbstractNum {

	private final List<Num> list;

	public ListSize(List<Num> list) {
		this.list = list;
	}

	@Override
	public int get() {
		return list.size();
	}

}
