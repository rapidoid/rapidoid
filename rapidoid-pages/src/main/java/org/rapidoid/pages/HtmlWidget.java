package org.rapidoid.pages;

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

import org.rapidoid.html.HTML;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagWidget;
import org.rapidoid.html.Tags;
import org.rapidoid.html.Var;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.pages.impl.DynamicContentWrapper;
import org.rapidoid.pages.impl.FileTemplate;
import org.rapidoid.pages.impl.MultiLanguageText;
import org.rapidoid.pages.impl.PageRenderer;
import org.rapidoid.pages.impl.SimpleHardcodedTag;
import org.rapidoid.util.U;

public abstract class HtmlWidget extends HTML implements TagWidget, PageComponent {

	private static final long serialVersionUID = -2760069277625837174L;

	private Tag<?> content;

	public void setContent(Tag<?> content) {
		this.content = content;
	}

	@Override
	public Tag<?> content() {
		return content;
	}

	public static Object _(String multiLanguageText, Object... formatArgs) {
		return new MultiLanguageText(multiLanguageText, formatArgs);
	}

	public static <T> T[] arr(T... arr) {
		return arr;
	}

	public static <T> Var<T> var(T value) {
		return Tags.var(value);
	}

	public static Tag<?> template(String templateFileName, Object... namesAndValues) {
		return new FileTemplate(templateFileName, namesAndValues);
	}

	public static Tag<?> dynamic(DynamicContent dynamic) {
		return new DynamicContentWrapper(dynamic);
	}

	public static Tag<?> hardcoded(String content) {
		return new SimpleHardcodedTag(content);
	}

	@Override
	public void render(HttpExchange x) {
		U.must(content != null, "No content was set in widget: " + super.toString());
		PageRenderer.get().render(Pages.ctx(x), content, x);
	}

	@Override
	public String toHTML(HttpExchange x) {
		U.must(content != null, "No content was set in widget: " + super.toString());
		return PageRenderer.get().toHTML(Pages.ctx(x), content, x);
	}

}
