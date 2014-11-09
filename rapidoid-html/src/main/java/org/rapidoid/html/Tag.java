package org.rapidoid.html;

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

public interface Tag<TAG extends Tag<?>> extends TagBase<TAG> {

	String id();

	TAG id(String id);

	String css();

	TAG css(String css);

	String role();

	TAG role(String role);

	boolean hidden();

	TAG hidden(boolean hidden);

	boolean enabled();

	TAG enabled(boolean value);

	TAG onClick(TagEventHandler<TAG> handler);

	TAG onClick(Action... action);

}
