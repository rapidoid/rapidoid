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

import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tags;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.pages.impl.PageRenderer;
import org.rapidoid.util.U;

public class EmitHandler implements Handler {

	@Override
	public Object handle(HttpExchange x) throws Exception {

		int event = U.num(x.data("event"));

		TagContext ctx = Pages.ctx(x);

		Map<Integer, Object> inp = Pages.inputs(x);
		ctx.emit(inp, event);

		Object page = U.newInstance(currentPage(x));

		ctx = Tags.context();
		x.setSession(Pages.SESSION_CTX, ctx);

		Object body = Pages.contentOf(x, page);

		if (body == null) {

		}

		if (body instanceof HttpExchange) {
			return body;
		}

		System.out.println(body);
		String html = PageRenderer.get().toHTML(ctx, body, x);

		Map<String, String> changes = U.map();
		changes.put("body", html);

		x.json();
		return changes;
	}

	private Class<?> currentPage(HttpExchange x) {
		return x.session(Pages.SESSION_CURRENT_PAGE);
	}

}
