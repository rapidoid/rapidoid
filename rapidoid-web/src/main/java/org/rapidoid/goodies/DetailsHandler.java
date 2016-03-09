package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;

import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DetailsHandler extends GUI implements Callable<Object> {

	private final String title;
	private final Object target;
	private final String[] properties;

	public DetailsHandler(String title, Object target, String... properties) {
		this.title = title;
		this.target = target;
		this.properties = properties;
	}

	@Override
	public Object call() throws Exception {
		return row(h1(title + ":"), show(target, properties));
	}

}
