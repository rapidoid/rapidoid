package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Text;

public class SimpleText extends AbstractText implements Text{

	private String value;

	public SimpleText(String value) {
		this.value = value;
	}

	@Override
	public String get() {
		return value;
	}

}
