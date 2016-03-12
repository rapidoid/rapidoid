package org.rapidoid.render;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RenderDSL {

	private final Template template;

	public RenderDSL(Template template) {
		this.template = template;
	}

	public String model(Object... model) {
		return template.render(model);
	}

}
