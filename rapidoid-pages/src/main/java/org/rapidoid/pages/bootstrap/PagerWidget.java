package org.rapidoid.pages.bootstrap;

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

import org.rapidoid.html.tag.DivTag;
import org.rapidoid.html.tag.LiTag;
import org.rapidoid.pages.Do;
import org.rapidoid.reactive.Var;

public class PagerWidget extends BootstrapWidget {

	private static final long serialVersionUID = 6880769731168102839L;

	public PagerWidget(int from, int to, Var<Integer> pageNumber) {

		LiTag first = li(a(span(LAQUO).click(Do.set(pageNumber, from)).attr("aria-hidden", "true"), span("First")
				.class_("sr-only")));

		LiTag prev = li(a(span(LT).click(Do.dec(pageNumber, 1)).attr("aria-hidden", "true"),
				span("Previous").class_("sr-only")));

		LiTag current = li(a("Page ", pageNumber, " of " + to));

		LiTag next = li(a(span(GT).click(Do.inc(pageNumber, 1)).attr("aria-hidden", "true"),
				span("Next").class_("sr-only")));

		LiTag last = li(a(span(RAQUO).click(Do.set(pageNumber, to)).attr("aria-hidden", "true"),
				span("Last").class_("sr-only")));

		DivTag pager = div(nav(ul(first, prev, current, next, last).class_("pagination"))).class_("pull-right");

		setContent(pager);
	}

}
