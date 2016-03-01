package org.rapidoid.http.customize;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.io.OutputStream;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DefaultJsonResponseRenderer implements JsonResponseRenderer {

	@Override
	public void renderJson(Object value, OutputStream out) throws Exception {
		// FIXME
	}

}
