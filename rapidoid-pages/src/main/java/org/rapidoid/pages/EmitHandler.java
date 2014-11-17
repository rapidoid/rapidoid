package org.rapidoid.pages;

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

import java.util.Map;

import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tags;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.pages.impl.PageRenderer;
import org.rapidoid.util.U;

public class EmitHandler implements Handler {

	private final Map<String, Class<?>> pages;

	public EmitHandler(Map<String, Class<?>> pages) {
		this.pages = pages;
	}

	@Override
	public Object handle(HttpExchange x) throws Exception {

		int event = U.num(x.data("event"));

		TagContext ctx = Pages.ctx(x);

		Map<Integer, Object> inp = Pages.inputs(x);
		ctx.emit(inp, event);

		Object page = U.newInstance(pages.get(x.session(Pages.SESSION_PAGE_NAME)));

		ctx = Tags.context();
		x.setSession(Pages.SESSION_CTX, ctx);

		Tag<?> body = Pages.contentOf(x, page);

		Map<String, String> changes = U.map();
		String html = PageRenderer.get().toHTML(ctx, body, x);
		changes.put("body", html);

		x.json();
		return changes;
	}

}
