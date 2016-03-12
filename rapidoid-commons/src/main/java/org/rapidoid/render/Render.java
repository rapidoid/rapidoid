package org.rapidoid.render;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Render {

	public static RenderDSL template(String templateSource) {
		return new RenderDSL(Templates.fromString(templateSource));
	}

	public static RenderDSL file(String filename) {
		return new RenderDSL(Templates.fromFile(filename));
	}

}
