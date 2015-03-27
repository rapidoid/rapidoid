package org.rapidoid.reactive.numList;

import java.util.ArrayList;
import java.util.List;

import org.rapidoid.reactive.Bool;
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

	@Override
	public Bool isEmpty() {
		return new ListIsEmpty(list);
	}

	@Override
	public Bool contains(Num element) {
		return new ListContains(list, element);
	}

	@Override
	public void add(Num element) {
		list.add(element);
	}

	@Override
	public void remove(Num element) {
		list.remove(element);
	}

	@Override
	public void removeAll(Num... elements) {
		for (Num element : elements) {
			list.remove(element);
		}
	}

	@Override
	public void addAll(Num... elements) {
		for (Num element : elements) {
			list.add(element);
		}
	}

}
