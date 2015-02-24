package org.rapidoid.reactive;

public interface Text {

	String get();

	Text plus(Text v);

	Text remove(Text v);

	Text replace(Text v);

	Text upper();

}
