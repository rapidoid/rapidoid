package org.rapidoid.jpa;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class JpaPageImpl extends RapidoidThing implements JpaPage {

	private final int from;
	private final int to;

	public JpaPageImpl(int from, int to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public int from() {
		return from;
	}

	@Override
	public int to() {
		return to;
	}

}
