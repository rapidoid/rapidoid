package org.rapidoid.pages.impl;

import java.io.OutputStream;

import org.rapidoid.annotation.Authors;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.impl.UndefinedTag;
import org.rapidoid.http.HttpExchange;

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
public abstract class HardcodedTag extends UndefinedTag {

	public abstract void render(TagContext ctx, HttpExchange x, PageRenderer renderer, OutputStream out);

	@Override
	public String tagKind() {
		return "hardcoded";
	}

}
