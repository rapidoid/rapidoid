package org.rapidoid.web.handler;

/*
 * #%L
 * rapidoid-web
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
import org.rapidoid.commons.Err;
import org.rapidoid.datamodel.Results;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.Grid;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.render.Render;
import org.rapidoid.u.U;
import org.rapidoid.web.config.bean.PageConfig;
import org.rapidoid.web.config.bean.PageGuiConfig;

import java.util.Map;
import java.util.regex.Pattern;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class PageHandler extends GenericHandler {

	private final PageConfig page;

	public PageHandler(PageConfig page) {
		this.page = page;
	}

	@Override
	public Object execute(Req req, Resp resp) {

		if (U.notEmpty(page.sql)) {
			return guiOf(page, sqlItems(page.sql));
		}

		if (U.notEmpty(page.gui)) {
			return guiModel(page.gui);
		}

		return GUI.N_A;
	}

	private Object guiModel(Map<String, PageGuiConfig> gui) {
		Map<Object, Object> model = U.map();

		for (Map.Entry<String, PageGuiConfig> e : gui.entrySet()) {
			model.put(e.getKey(), gui(e.getValue()));
		}

		return model;
	}

	private Object gui(PageGuiConfig gui) {
		Object item;

		switch (gui.type) {
			case grid:
				item = grid(gui, sqlItems(gui.sql));
				break;

			default:
				throw Err.notReady();
		}

		if (U.notEmpty(gui.caption)) {
			item = GUI.multi(GUI.titleBox(gui.caption), item);
		}

		if (U.notEmpty(gui.header) || U.notEmpty(gui.footer)) {
			item = GUI.panel(item).header(gui.header).footer(gui.footer);
		}

		return item;
	}

	private Object guiOf(PageConfig gui, Results items) {

		if (gui.single) {
			Object item = U.single(items);
			return GUI.details(item);
		}

		return grid(new PageGuiConfig(), items);
	}

	public Grid grid(final PageGuiConfig gui, Results items) {
		Req req = req();

		Grid grid = GUI.grid(items);

		String q = req.param("find", null);
		if (q != null) grid.highlightRegex(Pattern.quote(q));

		String highlight = req.param("$highlight", null);
		if (highlight != null) grid.highlightRegex(Pattern.quote(highlight));

		String pageSize = req.param("$pageSize", null);
		if (pageSize != null) grid.pageSize(U.num(pageSize));

		if (U.notEmpty(gui.uri)) {
			grid.toUri(new Mapper<Object, String>() {
				@Override
				public String map(Object item) throws Exception {
					return Render.template(gui.uri).model(item);
				}
			});
		}

		return grid;
	}

}
