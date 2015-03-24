package org.rapidoid.reactive.numList;

import java.util.ArrayList;
import java.util.List;

import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.NumList;

public class NumListImpl implements NumList {

	private final List<Num> list = new ArrayList<Num>();

	public NumListImpl(Num... elements) {
		for (Num e : elements) {
			list.add(e);
		}
	}

	@Override
	public Num get(Num index) {
		return new NumListAt(list, index);
	}

	@Override
	public void insert(int index, Num element) {
		list.add(index, element);
	}

	@Override
	public Num size() {
		return new ListSize(list);
	}

}
