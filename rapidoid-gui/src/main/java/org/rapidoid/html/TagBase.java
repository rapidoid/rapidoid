package org.rapidoid.html;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

/*
 * #%L
 * rapidoid-html
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
public interface TagBase<TAG extends Tag> {

	String tagKind();

	Object contents();

	TAG contents(Object... content);

	TAG append(Object... content);

	TAG prepend(Object... content);

	TAG copy();

	int size();

	boolean isEmpty();

	Object child(int index);

	TAG withChild(int index, Object child);

	String attr(String attr);

	TAG attr(String attr, String value);

	TAG attr(String attr, int value);

	TAG data(String dataAttr, String value);

	TAG data(String dataAttr, int value);

	TAG ng(String ngAttr, String value);

	boolean is(String attr);

	TAG is(String attr, boolean value);

	Tag extra(String attr, Object value);

	TAG cmd(String cmd, Object... args);

}
