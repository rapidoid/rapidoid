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

	private volatile long min;
	private volatile long max;
	private volatile boolean right;
	private volatile Long initial;

	public Pager(String param) {
		this.param = param;
	}

	@Override
	protected Tag render() {
		return shouldDisplay() ? pagination() : div();
	}

	protected String pageUri(long pageN) {
		IReqInfo req = req();

		Map<String, String> query = U.map(req.params());
		query.put(param, pageN + "");

		return GUI.uri(req.path(), query);
	}

	protected boolean shouldDisplay() {
		return U.neq(min, max);
	}

	protected Tag pagination() {
		long pageN = pageNumber();

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
			pagination = GUI.right(pagination);
		}

		return pagination;
	}

	protected long pageNumber() {
		Long pageNum = Cls.convert(req().params().get(param), Long.class);
		long value = U.or(pageNum, initial, min, 1L);
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

	public long min() {
		return min;
	}

	public Pager min(long min) {
		this.min = min;
		return this;
	}

	public long max() {
		return max;
	}

	public Pager max(long max) {
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

	public Long initial() {
		return initial;
	}

	public Pager initial(long initial) {
		this.initial = initial;
		return this;
	}
}
