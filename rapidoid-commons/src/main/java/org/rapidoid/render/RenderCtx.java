package org.rapidoid.render;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public interface RenderCtx {

	void print(String s);

	Object[] iter(String items);

	void val(String s, boolean escape);

	void valOr(String s, String or, boolean escape);

	void push(int index, Object v);

	void pop(int index, Object v);

	void call(String name);

	boolean cond(String name);

}
