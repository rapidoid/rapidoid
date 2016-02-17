package org.rapidoid.templates;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.io.OutputStream;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public interface ITemplate {

	void render(OutputStream output, Object... scopes);

	String render(Object... scopes);

}
