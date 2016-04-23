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
import org.rapidoid.cls.Cls;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.gui.reqinfo.IReqInfo;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.u.U;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Pager extends AbstractWidget<Pager> {

	private final String param;

	private volatile int min;
	private volatile int max;
	private volatile boolean right;
	private volatile Integer initial;

	public Pager(String param) {
		this.param = param;
	}

	@Override
	protected Tag render() {
		return shouldDisplay() ? pagination() : div();
	}

	protected String pageUri(int pageN) {
		IReqInfo req = req();

		Map<String, String> query = U.map(req.params());
		query.put(param, pageN + "");

		return GUI.uri(req.path(), query);
	}

	protected boolean shouldDisplay() {
		return U.neq(min, max);
	}

	protected Tag pagination() {
		int pageN = pageNumber();

		ATag first = first().href(pageUri(min));
		ATag prev = prev().href(pageUri(pageN - 1));
		ATag current = current();
		ATag next = next().href(pageUri(pageN + 1));
		ATag last = last().href(pageUri(max));

		Tag firstLi = pageN > min ? li(first) : li(first.href(null)).class_("disabled");
		Tag prevLi = pageN > min ? li(prev) : li(prev.href(null)).class_("disabled");
		Tag currentLi = li(current);
		Tag nextLi = pageN < max ? li(next) : li(next.href(null)).class_("disabled");
		Tag lastLi = pageN < max ? li(last) : li(last.href(null)).class_("disabled");

		Tag pagination = GUI.nav(GUI.ul_li(firstLi, prevLi, currentLi, nextLi, lastLi).class_("pagination"));

		if (right) {
			pagination = div(pagination).class_("pull-right");
		}

		return pagination;
	}

	protected int pageNumber() {
		Integer pageNum = Cls.convert(req().params().get(param), Integer.class);
		int value = U.or(pageNum, initial, min, 1);
		return U.bounds(min, value, max);
	}

	protected ATag first() {
		Tag firstIcon = span(GUI.LAQUO).attr("aria-hidden", "true");
		return a(firstIcon, span("First").class_("sr-only"));
	}

	protected ATag prev() {
		Tag prevIcon = span(GUI.LT).attr("aria-hidden", "true");
		return a(prevIcon, span("Previous").class_("sr-only"));
	}

	protected ATag current() {
		return GUI.a_void("Page ", pageNumber(), " of " + max);
	}

	protected ATag next() {
		Tag nextIcon = span(GUI.GT).attr("aria-hidden", "true");
		return a(nextIcon, span("Next").class_("sr-only"));
	}

	protected ATag last() {
		Tag lastIcon = span(GUI.RAQUO).attr("aria-hidden", "true");
		return a(lastIcon, span("Last").class_("sr-only"));
	}

	public String param() {
		return param;
	}

	public int min() {
		return min;
	}

	public Pager min(int min) {
		this.min = min;
		return this;
	}

	public int max() {
		return max;
	}

	public Pager max(int max) {
		this.max = max;
		return this;
	}

	public boolean right() {
		return right;
	}

	public Pager right(boolean right) {
		this.right = right;
		return this;
	}

	public Integer initial() {
		return initial;
	}

	public Pager initial(Integer initial) {
		this.initial = initial;
		return this;
	}
}
