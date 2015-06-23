package org.rapidoid.pages;

/*
 * #%L
 * rapidoid-pages
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagWidget;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchangeImpl;
import org.rapidoid.http.HttpResponses;
import org.rapidoid.http.session.InMemorySessionStore;
import org.rapidoid.net.impl.RapidoidConnection;
import org.rapidoid.pages.impl.PageRenderer;
import org.rapidoid.test.TestCommons;
import org.rapidoid.var.Var;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class PagesTestCommons extends TestCommons {

	@SuppressWarnings({ "unchecked" })
	protected static final Map<Integer, Object> NO_CHANGES = Collections.EMPTY_MAP;

	protected void print(Object content) {
		HttpExchange x = setupMockExchange();

		content = preprocess(content, x);

		String html = PageRenderer.get().toHTML(content, x);
		notNull(html);
		System.out.println(html);
	}

	@SuppressWarnings("unchecked")
	private Object preprocess(Object content, HttpExchange x) {
		if (content instanceof TagWidget<?>) {
			TagWidget<HttpExchange> widget = (TagWidget<HttpExchange>) content;
			content = widget.render(x);
			if (content == null) {
				return null;
			}
		}

		if (!(content instanceof Tag)) {
			content = Pages.page(x, content);
		}
		return content;
	}

	protected void has(Object content, String... containingTexts) {
		HttpExchange x = setupMockExchange();

		content = preprocess(content, x);

		String html = PageRenderer.get().toHTML(content, x);
		notNull(html);

		for (String text : containingTexts) {
			isTrue(html.contains(text));
		}
	}

	protected void hasRegex(Object content, String... containingRegexes) {
		HttpExchange x = setupMockExchange();

		content = preprocess(content, x);

		String html = PageRenderer.get().toHTML(content, x);

		for (String regex : containingRegexes) {
			isTrue(Pattern.compile(regex).matcher(html).find());
		}
	}

	protected void eq(Var<?> var, Object value) {
		eq(var.get(), value);
	}

	protected static HttpExchange setupMockExchange() {
		HttpExchangeImpl x = new HttpExchangeImpl();

		BufGroup bufs = new BufGroup(2);
		RapidoidConnection conn = new RapidoidConnection(null, bufs);
		x.setConnection(conn);

		InMemorySessionStore sessions = new InMemorySessionStore();
		x.init(new HttpResponses(false, false), sessions, null);

		Ctx.reset();
		Ctx.setExchange(x);
		return x;
	}

}
