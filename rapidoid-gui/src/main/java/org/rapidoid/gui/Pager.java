package org.rapidoid.gui;

/*
 * #%L
 * rapidoid-gui
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

	private volatile Integer min;
	private volatile Integer max;
	private volatile Integer initial;

	private volatile boolean right;

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
		return max == null || min == null || min < max;
	}

	protected Tag pagination() {
		int pageN = pageNumber();

		Tag firstLi = null;
		if (min != null) {
			ATag first = first().href(pageUri(min));
			firstLi = pageN > min ? li(first) : li(first.href(null)).class_("disabled");
		}

		Tag lastLi = null;
		if (max != null) {
			ATag last = last().href(pageUri(max));
			lastLi = pageN < max ? li(last) : li(last.href(null)).class_("disabled");
		}

		ATag prev = prev().href(pageUri(pageN - 1));
		Tag prevLi = min == null || pageN > min ? li(prev) : li(prev.href(null)).class_("disabled");

		ATag current = current();
		Tag currentLi = li(current);

		ATag next = next().href(pageUri(pageN + 1));
		Tag nextLi = max == null || pageN < max ? li(next) : li(next.href(null)).class_("disabled");

		Tag pagination = GUI.nav(GUI.ul_li(firstLi, prevLi, currentLi, nextLi, lastLi).class_("pagination"));

		if (right) {
			pagination = GUI.right(pagination);
		}

		return pagination;
	}

	protected int pageNumber() {
		Integer pageNum = Cls.convert(req().params().get(param), Integer.class);
		int value = U.or(pageNum, initial, min, 1);

		if (min != null) {
			value = Math.max(min, value);
		}

		if (max != null) {
			value = Math.min(max, value);
		}

		return value;
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
		String pageInfo = "Page " + pageNumber();

		if (max != null) {
			pageInfo += " of " + max;
		}

		return GUI.a_void(pageInfo);
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

	public Pager initial(int initial) {
		this.initial = initial;
		return this;
	}
}
