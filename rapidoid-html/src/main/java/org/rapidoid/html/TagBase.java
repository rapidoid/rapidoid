package org.rapidoid.html;

import org.rapidoid.var.Var;

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

public interface TagBase<TAG extends Tag<?>> {

	String tagKind();

	Object content();

	TAG content(Object... content);

	TAG append(Object... content);

	TAG prepend(Object... content);

	TAG copy();

	int size();

	Object child(int index);

	void setChild(int index, Object child);

	String attr(String attr);

	TAG attr(String attr, String value);

	boolean is(String attr);

	TAG is(String attr, boolean value);

	TAG bindContent(Var<Object> var);

	TAG bind(String attr, Var<String> var);

	TAG bindIs(String attr, Var<Boolean> var);

	TAG unbindContent(String attr);

	TAG unbind(String attr);

}
