package org.rapidoid.reactive;

public interface NumList {

	Num get(Num index);

	void insert(int index, Num element);

	Num size();

	Bool isEmpty();

	Bool contains(Num element);

	void add(Num element);

	void remove(Num element);

	void removeAll(Num... elements);

	void addAll(Num... elements);

}
