package org.rapidoid.validation;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class InvalidData extends RuntimeException {

	public InvalidData(String msg) {
		super(msg);
	}

}
