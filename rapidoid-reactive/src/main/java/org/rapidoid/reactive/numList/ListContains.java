package org.rapidoid.reactive.numList;

import java.util.List;

import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.bool.AbstractBool;

public class ListContains extends AbstractBool {

	private final List<Num> list;
	private final Num element;

	public ListContains(List<Num> list, Num element) {
		this.list = list;
		this.element = element;
	}

	@Override
	public boolean get() {
		return list.contains(element);
	}

}
