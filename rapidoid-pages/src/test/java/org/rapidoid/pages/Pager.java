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

import org.rapidoid.pages.html.ButtonTag;
import org.rapidoid.pages.html.SpanTag;

public class Pager extends AbstractPage {

	private int from;
	private int to;
	private Var<Integer> page;

	public Pager(int from, int to, Var<Integer> page) {
		this.from = from;
		this.to = to;
		this.page = page;
	}

	@Override
	protected Object contents() {
		ButtonTag first = button("<<", Do.set(page, from));
		ButtonTag prev = button("<", Do.dec(page, 1));
		SpanTag current = span("(page ", page, ")");
		ButtonTag next = button(">", Do.inc(page, 1));
		ButtonTag last = button(">>", Do.set(page, to));
		return span(first, prev, current, next, last);
	}

}
