package org.rapidoid.http.customize;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.io.Res;
import org.rapidoid.render.Templates;

import java.io.OutputStream;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DefaultViewRenderer implements ViewRenderer {

	@Override
	public boolean render(Req req, Resp resp, OutputStream out) throws Exception {
		Res template = Templates.resource(resp.view() + ".html");

		if (!template.exists()) {
			return false;
		}

		Templates.fromRes(template).renderTo(out, resp.model());
		return true;
	}

}
