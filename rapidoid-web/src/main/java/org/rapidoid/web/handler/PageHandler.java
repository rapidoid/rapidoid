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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.Grid;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.u.U;
import org.rapidoid.web.config.bean.PageConfig;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class PageHandler extends RapidoidThing implements ReqRespHandler {

	private final PageConfig page;

	public PageHandler(PageConfig page) {
		this.page = page;
	}

	@Override
	public Object execute(Req req, Resp resp) {
		List<Map<String, Object>> items = JDBC.query(page.sql, req.params());

		Grid grid = GUI.grid(items);

		String q = req.param("find", null);
		if (q != null) grid.highlightRegex(Pattern.quote(q));

		String highlight = req.param("$highlight", null);
		if (highlight != null) grid.highlightRegex(Pattern.quote(highlight));

		String pageSize = req.param("$pageSize", null);
		if (pageSize != null) grid.pageSize(U.num(pageSize));

		return grid;
	}

}
