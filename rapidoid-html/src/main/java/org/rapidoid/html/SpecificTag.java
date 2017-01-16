package org.rapidoid.html;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.Special;

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
public interface SpecificTag<TAG extends SpecificTag<?>> extends Tag {

	@Special
	TAG contents(Object... content);

	@Special
	TAG append(Object... content);

	@Special
	TAG prepend(Object... content);

	@Special
	TAG copy();

	@Special
	TAG withChild(int index, Object child);

	@Special
	TAG attr(String attr, String value);

	@Special
	TAG is(String attr, boolean value);

	@Special
	TAG cmd(String cmd, Object... args);

	@Special
	TAG navigate(String cmd, Object... args);

	String id();

	TAG id(String id);

	String style();

	TAG style(String css);

	String class_();

	TAG class_(String class_);

	String role();

	TAG role(String role);

	String onclick();

	TAG onclick(String onclick);

	boolean hidden();

	TAG hidden(boolean hidden);

	boolean disabled();

	TAG disabled(boolean value);

}
