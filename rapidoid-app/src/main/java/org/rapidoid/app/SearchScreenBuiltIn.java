package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.util.List;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.html.Tag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.model.Items;
import org.rapidoid.plugins.DB;
import org.rapidoid.util.U;
import org.rapidoid.widget.GridWidget;
import org.rapidoid.widget.HighlightedGridWidget;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class SearchScreenBuiltIn extends Screen {

	public Object content(HttpExchange x) {

		final String query = x.param("q", "");
		List<?> found = DB.fullTextSearch(query);
		Items items = beanItems(Object.class, found.toArray());

		Tag queryInfo = !U.isEmpty(query) ? span(" for ", b(highlight(query))) : null;
		Tag title = titleBox("Total " + found.size() + " search results", queryInfo);

		String regex = "(?i)" + Pattern.quote(query);
		GridWidget grid = new HighlightedGridWidget(items, "", 10, "id", "_class", "_str").regex(regex);

		return div(title, grid);
	}

}
