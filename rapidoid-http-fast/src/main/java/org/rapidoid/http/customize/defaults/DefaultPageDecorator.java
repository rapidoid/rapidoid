package org.rapidoid.http.customize.defaults;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.http.customize.PageDecorator;
import org.rapidoid.u.U;
import org.rapidoid.writable.WritableUtils;
import org.rapidoid.writable.WritableOutputStream;
import org.rapidoid.web.Screen;

import java.io.OutputStream;
import java.util.regex.Pattern;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class DefaultPageDecorator extends RapidoidThing implements PageDecorator {

	private static final String FULL_PAGE_REGEX = "(?s)^(?:\\s*(<!--(?:.*?)-->)*?)*?<(!DOCTYPE\\s+html|html)>";

	private static final Pattern FULL_PAGE_PATTERN = Pattern.compile(FULL_PAGE_REGEX);

	@Override
	public void renderPage(Req req, String content, OutputStream out) throws Exception {
		U.notNull(content, "page content");

		Resp resp = req.response();

		if (isFullPage(req, content)) {
			WritableUtils.writeUTF8(new WritableOutputStream(out), content);
			return;
		}

		Screen screen = resp.screen();
		screen.content(GUI.hardcoded(content));

		screen.render(out);
	}

	private boolean isFullPage(Req req, String content) {
		return (req.attr("_embedded", false) && content.startsWith("<!--EMBEDDED-->"))
			|| FULL_PAGE_PATTERN.matcher(content).find();
	}

}
