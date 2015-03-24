package org.rapidoid.reactive.numList;

import java.util.List;

import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.bool.AbstractBool;

public class ListIsEmpty extends AbstractBool {

	private final List<Num> list;

	public ListIsEmpty(List<Num> list) {
		this.list = list;
	}

	@Override
	public boolean get() {
		return list.isEmpty();
	}

}
