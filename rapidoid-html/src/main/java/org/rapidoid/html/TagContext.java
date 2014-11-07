package org.rapidoid.html;

import java.util.Map;

import org.rapidoid.html.impl.TagData;

/*
 * #%L
 * rapidoid-html
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

public interface TagContext {

	void emit(String hnd, String event);

	String getNewId(TagData<?> tag);

	Tag<?> get(String hnd);

	Map<String, Tag<?>> changedTags();

	void changedContents(TagData<?> tagData);

	Map<String, String> changedContent();

	void add(Tag<?> tag);

}
