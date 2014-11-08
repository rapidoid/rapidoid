package org.rapidoid.pages.bootstrap;

import org.rapidoid.html.Bootstrap;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagWidget;
import org.rapidoid.html.Var;
import org.rapidoid.html.impl.TagRenderer;
import org.rapidoid.pages.DynamicContent;
import org.rapidoid.pages.HtmlWidget;
import org.rapidoid.util.U;

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

public abstract class BootstrapWidget extends Bootstrap implements TagWidget {

	private Tag<?> content;

	public void setContent(Tag<?> content) {
		this.content = content;
	}

	@Override
	public Tag<?> content() {
		return content;
	}

	@Override
	public void attachContext(TagContext ctx) {
		ctx.add(content());
	}

	@Override
	public String toString() {
		U.must(content != null, "No content was set in widget: " + super.toString());
		return TagRenderer.get().str(content, null);
	}

	public static Object _(String multiLanguageText, Object... formatArgs) {
		return HtmlWidget._(multiLanguageText, formatArgs);
	}

	public static <T> T[] arr(T... arr) {
		return HtmlWidget.arr(arr);
	}

	public static <T> Var<T> var(T value) {
		return HtmlWidget.var(value);
	}

	public static Tag<?> template(String templateFileName, Object... namesAndValues) {
		return HtmlWidget.template(templateFileName, namesAndValues);
	}

	public static Tag<?> dynamic(DynamicContent dynamic) {
		return HtmlWidget.dynamic(dynamic);
	}

}
