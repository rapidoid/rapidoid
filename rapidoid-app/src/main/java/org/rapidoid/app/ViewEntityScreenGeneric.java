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

import org.rapidoid.annotation.Session;
import org.rapidoid.db.DB;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.pages.Pages;
import org.rapidoid.util.U;

public class ViewEntityScreenGeneric extends AppGUI {

	@Session
	private Object entity;

	public Object content(HttpExchange x) {

		Tag caption = h2(U.capitalized(x.pathSegment(0)) + " Details");

		long id = Long.parseLong(x.pathSegment(1));
		entity = DB.get(id);

		FormTag details = view(entity, EDIT_BACK);

		return row(caption, details);
	}

	public void onBack(HttpExchange x) {
		Pages.goBack(x);
	}

	public void onEdit(HttpExchange x) {
		String id = x.pathSegment(1);
		x.redirect("/edit" + entity.getClass().getSimpleName() + "/" + id);
	}
}
