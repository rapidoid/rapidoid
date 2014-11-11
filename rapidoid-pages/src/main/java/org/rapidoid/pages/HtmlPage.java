package org.rapidoid.pages;

import org.rapidoid.html.Tag;

/*
 * #%L
 * rapidoid-pages
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

public abstract class HtmlPage extends HtmlWidget implements Page {

	private static final long serialVersionUID = -4604288833708886704L;

	@Override
	public Tag<?> page() {
		return template("jquery-page.html", "title", pageTitle(), "style", pageStyle(), "head", pageHead(), "body",
				pageBody());
	}

	protected abstract Object pageBody();

	protected Object pageHead() {
		return "";
	}

	protected Object pageStyle() {
		return "";
	}

	protected String pageTitle() {
		return Pages.pageTitle(getClass());
	}

}
