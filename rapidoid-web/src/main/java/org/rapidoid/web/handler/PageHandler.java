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
import org.rapidoid.datamodel.impl.NoResults;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.Grid;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.render.Render;
import org.rapidoid.u.U;
import org.rapidoid.web.config.bean.GuiConfig;
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
			return gui(page);
		}

		if (U.notEmpty(page.gui)) {
			return guiModel(page.gui);
		}

		return GUI.N_A;
	}

	private Object guiModel(Map<String, PageGuiConfig> gui) {
		Map<Object, Object> model = U.map();

		for (Map.Entry<String, PageGuiConfig> e : gui.entrySet()) {

			PageGuiConfig cfg = e.getValue();
			Object data = fetchData(cfg);
			Object widget = createWidget(cfg, data);

			// put data in model (e.g. ${books_data})
			model.put(e.getKey() + "_data", data);

			// put widget in model (e.g. @{books})
			model.put(e.getKey(), widget);
		}

		return model;
	}

	private Object fetchData(GuiConfig gui) {
		if (U.notEmpty(gui.sql())) {
			if (gui.single()) {
				return sqlItems(gui.sql()).single();
			} else {
				return sqlItems(gui.sql());
			}

		} else {
			return new NoResults<>();
		}
	}

	private Object gui(GuiConfig gui) {
		Object data = fetchData(gui);
		return createWidget(gui, data);
	}

	private Object createWidget(GuiConfig gui, Object data) {
		Object item = createWidgetByType(gui, data);
		item = wrapWidget(gui, item);
		return item;
	}

	private Object wrapWidget(GuiConfig gui, Object item) {

		if (U.notEmpty(gui.caption())) {
			item = GUI.multi(GUI.titleBox(gui.caption()), item);
		}

		if (U.notEmpty(gui.header()) || U.notEmpty(gui.footer())) {
			item = GUI.panel(item).header(gui.header()).footer(gui.footer());
		}

		return item;
	}

	private Object createWidgetByType(GuiConfig gui, Object data) {
		switch (gui.type()) {

			case grid:

				if (gui.single()) {
					return GUI.details(data);

				} else {
					Grid grid = grid(gui, (Results) data);
					if (gui.pageSize() > 0) grid.pageSize(gui.pageSize());
					return grid;
				}

			default:
				throw Err.notReady();
		}
	}

	public Grid grid(final GuiConfig gui, Results items) {
		Req req = req();

		Grid grid = GUI.grid(items);

		String q = req.param("find", null);
		if (q != null) grid.highlightRegex(Pattern.quote(q));

		String highlight = req.param("$highlight", null);
		if (highlight != null) grid.highlightRegex(Pattern.quote(highlight));

		String pageSize = req.param("$pageSize", null);
		if (pageSize != null) grid.pageSize(U.num(pageSize));

		if (U.notEmpty(gui.uri())) {
			grid.toUri(new Mapper<Object, String>() {
				@Override
				public String map(Object item) throws Exception {
					return Render.template(gui.uri()).model(item);
				}
			});
		}

		return grid;
	}

}
