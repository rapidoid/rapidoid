package org.rapidoid.pages;

/*
 * #%L
 * rapidoid-pages
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

public interface Tag<TAG extends Tag<?>> {

	String str(int level);

	Object content();

	TAG content(Object... content);

	TAG append(Object... content);

	TAG prepend(Object... content);

	String id();

	TAG id(String id);

	String css();

	TAG css(String css);

	boolean hidden();

	TAG hidden(boolean hidden);

	boolean enabled();

	TAG enabled(boolean value);

	TAG classs(String classs);

	TAG classIf(boolean condition, String thenCls, String elseCls);

	TAG contentIf(boolean condition, Object thenCnt, Object elseCnt);

	TAG onClick(Handler<TAG> handler);

	TAG onClick(Action... action);

	Action doShow();

	Action doHide();

	Action doEnable();

	Action doDisable();

	Action doRemove();

	Action doAdd(Object... tags);

}
