package org.rapidoid.docs.customform;

import org.rapidoid.annotation.Page;
import org.rapidoid.annotation.Web;
import org.rapidoid.app.GUI;
import org.rapidoid.widget.ButtonWidget;
import org.rapidoid.widget.FormWidget;

/*
 * #%L
 * rapidoid-docs
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

@Web
public class CustomForm extends GUI {

	@Page(url = "/")
	public Object content() {
		Movie movie = new Movie();
		FormWidget f = create(movie, "year");
		ButtonWidget changeYear = btn("Change year").command("NewYear").primary();
		f = f.buttons(btn("Ab"), changeYear, btn("!Efg").danger());
		return f;
	}

	public void onNewYear() {
		// DB.update(movie);
	}
}
