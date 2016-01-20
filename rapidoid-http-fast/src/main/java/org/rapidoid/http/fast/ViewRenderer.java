package org.rapidoid.http.fast;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;

@Authors("Nikolche Mihajlovski")
@Since("5.0.11")
public interface ViewRenderer {

	void render(Req req, Resp resp) throws Exception;

}
