package org.rapidoid.pages.impl;

/*
 * #%L
 * rapidoid-pages
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

import java.io.OutputStream;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.html.HTML;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagProcessor;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.plugins.templates.ITemplate;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class FileTemplateTag extends HardcodedTag {

	private final ITemplate template;

	private final Object[] namesAndValues;

	public FileTemplateTag(ITemplate template, Object[] namesAndValues) {
		this.template = template;
		this.namesAndValues = namesAndValues;
	}

	@Override
	public void render(HttpExchange x, PageRenderer renderer, OutputStream out) {

		Map<String, Object> scope = U.map();

		for (int i = 0; i < namesAndValues.length / 2; i++) {
			String placeholder = (String) namesAndValues[i * 2];
			Object value = namesAndValues[i * 2 + 1];
			U.must(!(value instanceof ITemplate));

			value = renderer.toHTML(value, x);
			scope.put(placeholder, value);
		}

		template.render(out, scope, x.model());
	}

	@Override
	public Tag copy() {
		return new FileTemplateTag(template, namesAndValues);
	}

	@Override
	public void traverse(TagProcessor<Tag> processor) {
		for (int i = 0; i < namesAndValues.length / 2; i++) {
			Object val = namesAndValues[i * 2 + 1];
			HTML.traverse(val, processor);
		}
	}

	@Override
	public String tagKind() {
		return "template";
	}

}
