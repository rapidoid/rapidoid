package org.rapidoid.http;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.0.11")
public interface ViewRenderer {

	void render(Req req, Resp resp) throws Exception;

}
