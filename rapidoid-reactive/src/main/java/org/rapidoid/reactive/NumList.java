package org.rapidoid.reactive;

public interface NumList {

	Num get(Num index);

	void insert(int index, Num element);

	Num size();

	Bool isEmpty();

	Bool contains(Num element);

}
