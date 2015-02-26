package org.rapidoid.reactive;

public interface Text {

	String get();

	Text plus(Text v);

	Text remove(Text v);

	Text replace(Text v1, Text v2);

	Text upper();

	Text lower();

	Text trim();

}
