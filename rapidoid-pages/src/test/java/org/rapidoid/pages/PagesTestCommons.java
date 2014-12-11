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

import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

import org.rapidoid.buffer.BufGroup;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchangeImpl;
import org.rapidoid.http.HttpExchanges;
import org.rapidoid.http.HttpResponses;
import org.rapidoid.http.InMemoryHttpSession;
import org.rapidoid.net.impl.RapidoidConnection;
import org.rapidoid.pages.impl.PageRenderer;
import org.rapidoid.test.TestCommons;
import org.rapidoid.var.Var;

public class PagesTestCommons extends TestCommons {

	@SuppressWarnings({ "unchecked" })
	protected static final Map<Integer, Object> NO_CHANGES = Collections.EMPTY_MAP;

	protected void print(TagContext ctx, Object content) {
		HttpExchange x = mockExchange(ctx);

		if (!(content instanceof Tag<?>)) {
			content = Pages.page(x, content);
		}

		String html = PageRenderer.get().toHTML(ctx, content, x);
		notNull(html);
		System.out.println(html);
	}

	protected void has(TagContext ctx, Object content, String... containingTexts) {
		HttpExchange x = mockExchange(ctx);

		if (!(content instanceof Tag<?>)) {
			content = Pages.page(x, content);
		}

		String html = PageRenderer.get().toHTML(ctx, content, x);
		notNull(html);

		for (String text : containingTexts) {
			isTrue(html.contains(text));
		}
	}

	protected void hasRegex(TagContext ctx, Object content, String... containingRegexes) {
		HttpExchange x = mockExchange(ctx);

		if (!(content instanceof Tag<?>)) {
			content = Pages.page(x, content);
		}

		String html = PageRenderer.get().toHTML(ctx, content, x);

		for (String regex : containingRegexes) {
			isTrue(Pattern.compile(regex).matcher(html).find());
		}
	}

	protected void eq(Var<?> var, Object value) {
		eq(var.get(), value);
	}

	protected static HttpExchange mockExchange(TagContext ctx) {
		HttpExchangeImpl x = new HttpExchangeImpl();

		BufGroup bufs = new BufGroup(2);
		RapidoidConnection conn = new RapidoidConnection(null, bufs);
		x.setConnection(conn);

		InMemoryHttpSession session = new InMemoryHttpSession();
		session.openSession("sess1");
		session.setAttribute("sess1", Pages.SESSION_CTX, ctx);
		x.setSession(session);

		x.setResponses(new HttpResponses(false, false));

		HttpExchanges.setThreadLocalExchange(x);
		return x;
	}

}
