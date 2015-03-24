package org.rapidoid.reactive;

public interface Text {

	String get();

	Text plus(Text v);

	Text remove(Text v);

	Text replace(Text v1, Text v2);

	Text upper();

	Text lower();

	Text trim();

	Text substring(Num beginIndex, Num endIndex);

	Num length();

	Num indexOf(Text v);

	Text mid(Num beginIndex, Num endIndex);

	Bool contains(Text v);

	Bool endsWith(Text v);

	Bool startsWith(Text v);

	Bool isEmpty();

	Bool eq(Text v);

	Num lastIndexOf(Text v);

	Num parseInt();

}
