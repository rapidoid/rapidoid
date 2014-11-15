package org.rapidoid.pages.impl;

/*
 * #%L
 * rapidoid-pages
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.io.OutputStream;

import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.impl.TagRenderer;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.pages.PageWidget;

public class PageRenderer extends TagRenderer {

	private static final PageRenderer INSTANCE = new PageRenderer();

	public static PageRenderer get() {
		return INSTANCE;
	}

	@Override
	public void str(TagContext ctx, Object content, int level, boolean inline, Object extra, OutputStream out) {
		if (content instanceof HardcodedTag) {
			HardcodedTag hardcoded = ((HardcodedTag) content);
			hardcoded.render(ctx, (HttpExchange) extra, this, out);
		} else if (content instanceof PageWidget) {
			PageWidget widget = ((PageWidget) content);
			Tag<?> view = widget.view((HttpExchange) extra);
			str(ctx, view, level, inline, extra, out);
		} else {
			super.str(ctx, content, level, inline, extra, out);
		}
	}

	public void render(TagContext ctx, Object content, HttpExchange x) {
		str(ctx, content, x, x.outputStream());
	}

}
