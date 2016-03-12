package org.rapidoid.integrate;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Integrate {

	public static MustacheViewRenderer mustacheViewRenderer() {
		return new MustacheViewRenderer();
	}

}
