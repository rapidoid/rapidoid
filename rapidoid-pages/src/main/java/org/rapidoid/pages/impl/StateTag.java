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

import java.io.IOException;
import java.io.OutputStream;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagProcessor;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.json.JSON;
import org.rapidoid.pages.Pages;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class StateTag extends HardcodedTag {

	private final HttpExchange x;

	public StateTag(HttpExchange x) {
		this.x = x;
	}

	@Override
	public void render(HttpExchange x, PageRenderer renderer, OutputStream out) {
		String json = JSON.jacksonStringify(Pages.stateOf(x));
		try {
			out.write(json.getBytes());
		} catch (IOException e) {
			throw U.rte("Cannot render state tag!", e);
		}
	}

	@Override
	public Tag copy() {
		return new StateTag(x);
	}

	@Override
	public void traverse(TagProcessor<Tag> processor) {}

	@Override
	public String tagKind() {
		return "state";
	}

}
