package org.rapidoid.model.impl;

import org.rapidoid.model.Property;
import org.rapidoid.u.U;

import java.util.Set;

public abstract class AbstractProperty implements Property {

	private final Set<String> errors = U.set();

	@Override
	public Set<String> errors() {
		return errors;
	}

}
