package org.rapidoid.pages.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.util.U;

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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class SimpleHardcodedTag extends HardcodedTag {

	private final byte[] content;

	public SimpleHardcodedTag(String content) {
		this(content.getBytes());
	}

	public SimpleHardcodedTag(byte[] content) {
		this.content = content;
	}

	@Override
	public void render(TagContext ctx, HttpExchange x, PageRenderer renderer, OutputStream out) {
		try {
			out.write(content);
		} catch (IOException e) {
			throw U.rte("Cannot render hardcoded tag!", e);
		}
	}

	@Override
	public Tag copy() {
		return new SimpleHardcodedTag(content);
	}

}
