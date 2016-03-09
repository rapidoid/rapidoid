package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;

import java.util.List;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class MultiDetailsHandler extends GUI implements Callable<Object> {

	private final String title;
	private final List<?> items;
	private final String[] properties;

	public MultiDetailsHandler(String title, List<?> items, String... properties) {
		this.title = title;
		this.items = items;
		this.properties = properties;
	}

	@Override
	public Object call() throws Exception {
		return row(h1(title + ":"), grid(items, properties));
	}

}
