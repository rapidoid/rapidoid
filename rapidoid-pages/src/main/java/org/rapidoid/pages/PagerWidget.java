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

import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.var.Var;

public class PagerWidget extends AbstractWidget {

	private int from;
	private int to;
	private Var<Integer> pageNumber;

	public PagerWidget(int from, int to, Var<Integer> pageNumber) {
		this.from = from;
		this.to = to;
		this.pageNumber = pageNumber;
	}

	@Override
	protected Tag create() {
		int pageN = pageNumber.get();

		Tag firstIcon = span(LAQUO).attr("aria-hidden", "true");
		ATag first = a_void(firstIcon, span("First").class_("sr-only")).cmd("_set", pageNumber, from);

		Tag prevIcon = span(LT).attr("aria-hidden", "true");
		ATag prev = a_void(prevIcon, span("Previous").class_("sr-only")).cmd("_dec", pageNumber, 1);

		ATag current = a_void("Page ", pageN, " of " + to);

		Tag nextIcon = span(GT).attr("aria-hidden", "true");
		ATag next = a_void(nextIcon, span("Next").class_("sr-only")).cmd("_inc", pageNumber, 1);

		Tag lastIcon = span(RAQUO).attr("aria-hidden", "true");
		ATag last = a_void(lastIcon, span("Last").class_("sr-only")).cmd("_set", pageNumber, to);

		Tag firstLi = pageN > from ? li(first) : li(first.cmd(null)).class_("disabled");
		Tag prevLi = pageN > from ? li(prev) : li(prev.cmd(null)).class_("disabled");
		Tag currentLi = li(current);
		Tag nextLi = pageN < to ? li(next) : li(next.cmd(null)).class_("disabled");
		Tag lastLi = pageN < to ? li(last) : li(last.cmd(null)).class_("disabled");

		Tag pagination = nav(ul_li(firstLi, prevLi, currentLi, nextLi, lastLi).class_("pagination"));
		return div(pagination).class_("pull-right");
	}

}
