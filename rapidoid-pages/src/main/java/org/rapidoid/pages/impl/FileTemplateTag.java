package org.rapidoid.pages.impl;

/*
 * #%L
 * rapidoid-pages
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.io.IOException;
import java.io.OutputStream;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.html.HTML;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.TagProcessor;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.util.IO;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class FileTemplateTag extends HardcodedTag {

	private final String templateName;

	private final Object[] namesAndValues;

	public FileTemplateTag(String templateName, Object[] namesAndValues) {
		U.must(IO.resource(templateName) != null, "Cannot find file: %s", templateName);

		this.templateName = templateName;
		this.namesAndValues = namesAndValues;
	}

	@Override
	public void render(TagContext ctx, HttpExchange x, PageRenderer renderer, OutputStream out) {
		String text = IO.loadResourceAsString(templateName);

		for (int i = 0; i < namesAndValues.length / 2; i++) {
			String placeholder = (String) namesAndValues[i * 2];
			String value = renderer.toHTML(ctx, namesAndValues[i * 2 + 1], x);

			text = UTILS.fillIn(text, placeholder, value);
		}

		try {
			out.write(text.getBytes());
		} catch (IOException e) {
			throw U.rte("Cannot render template!", e);
		}
	}

	@Override
	public Tag copy() {
		return new FileTemplateTag(templateName, namesAndValues);
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
