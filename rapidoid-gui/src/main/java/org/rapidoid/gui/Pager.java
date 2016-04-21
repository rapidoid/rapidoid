package org.rapidoid.gui;

/*
 * #%L
 * rapidoid-gui
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.html.Tag;
import org.rapidoid.var.Var;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Pager extends AbstractWidget<Pager> {

	private int from;
	private int to;
	private Var<Integer> pageNumber;

	public Pager(int from, int to, Var<Integer> pageNumber) {
		this.from = from;
		this.to = to;
		this.pageNumber = pageNumber;
	}

	@Override
	protected Tag render() {
		Tag first = first().cmd("_set", pageNumber, from);
		Tag prev = prev().cmd("_dec", pageNumber, 1);
		Tag current = current();
		Tag next = next().cmd("_inc", pageNumber, 1);
		Tag last = last().cmd("_set", pageNumber, to);

		return shouldDisplay() ? pagination(first, prev, current, next, last) : div();
	}

	protected boolean shouldDisplay() {
		return to > 1;
	}

	protected Tag pagination(Tag first, Tag prev, Tag current, Tag next, Tag last) {
		int pageN = pageNumber();

		Tag firstLi = pageN > from ? li(first) : li(first.cmd(null)).class_("disabled");
		Tag prevLi = pageN > from ? li(prev) : li(prev.cmd(null)).class_("disabled");
		Tag currentLi = li(current);
		Tag nextLi = pageN < to ? li(next) : li(next.cmd(null)).class_("disabled");
		Tag lastLi = pageN < to ? li(last) : li(last.cmd(null)).class_("disabled");

		Tag pagination = GUI.nav(GUI.ul_li(firstLi, prevLi, currentLi, nextLi, lastLi).class_("pagination"));
		return div(pagination).class_("pull-right");
	}

	protected int pageNumber() {
		return pageNumber.get();
	}

	protected Tag first() {
		Tag firstIcon = span(GUI.LAQUO).attr("aria-hidden", "true");
		return GUI.a_void(firstIcon, span("First").class_("sr-only"));
	}

	protected Tag prev() {
		Tag prevIcon = span(GUI.LT).attr("aria-hidden", "true");
		return GUI.a_void(prevIcon, span("Previous").class_("sr-only"));
	}

	protected Tag current() {
		return GUI.a_void("Page ", pageNumber(), " of " + to);
	}

	protected Tag next() {
		Tag nextIcon = span(GUI.GT).attr("aria-hidden", "true");
		return GUI.a_void(nextIcon, span("Next").class_("sr-only"));
	}

	protected Tag last() {
		Tag lastIcon = span(GUI.RAQUO).attr("aria-hidden", "true");
		return GUI.a_void(lastIcon, span("Last").class_("sr-only"));
	}

}
