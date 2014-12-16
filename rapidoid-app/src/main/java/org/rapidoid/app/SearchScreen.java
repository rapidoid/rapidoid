package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
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

import java.util.List;
import java.util.regex.Pattern;

import org.rapidoid.db.DB;
import org.rapidoid.html.Tag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.model.Items;
import org.rapidoid.pages.GridWidget;
import org.rapidoid.pages.HighlightedGridWidget;

public class SearchScreen extends AppGUI {

	public Object content(HttpExchange x) {

		final String query = x.param("q");
		List<Object> found = DB.find(query);
		Items items = beanItems(Object.class, found.toArray());

		Tag title = h3("Total " + found.size() + " search results for ", b(highlight(query)), ":");

		String regex = "(?i)" + Pattern.quote(query);
		GridWidget grid = new HighlightedGridWidget(items, "", 10, "id", "_class", "_str").regex(regex);

		return div(title, grid);
	}

}
