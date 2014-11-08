package org.rapidoid.html.impl;

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

import org.rapidoid.html.Action;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagEventHandler;
import org.rapidoid.util.U;

public abstract class UnmodifiableTag implements Tag<Tag<?>> {

	@Override
	public Object content() {
		throw U.notExpected();
	}

	@Override
	public Tag<?> content(Object... content) {
		throw U.notExpected();
	}

	@Override
	public Tag<?> append(Object... content) {
		throw U.notExpected();
	}

	@Override
	public Tag<?> prepend(Object... content) {
		throw U.notExpected();
	}

	@Override
	public String id() {
		throw U.notExpected();
	}

	@Override
	public Tag<?> id(String id) {
		throw U.notExpected();
	}

	@Override
	public String css() {
		throw U.notExpected();
	}

	@Override
	public Tag<?> css(String css) {
		throw U.notExpected();
	}

	@Override
	public boolean hidden() {
		throw U.notExpected();
	}

	@Override
	public Tag<?> hidden(boolean hidden) {
		throw U.notExpected();
	}

	@Override
	public boolean enabled() {
		throw U.notExpected();
	}

	@Override
	public Tag<?> enabled(boolean value) {
		throw U.notExpected();
	}

	@Override
	public Tag<?> attr(String attr, String value) {
		throw U.notExpected();
	}

	@Override
	public String attr(String attr) {
		throw U.notExpected();
	}

	@Override
	public Tag<?> classs(String classs) {
		throw U.notExpected();
	}

	@Override
	public Tag<?> classIf(boolean condition, String thenCls, String elseCls) {
		throw U.notExpected();
	}

	@Override
	public Tag<?> contentIf(boolean condition, Object thenCnt, Object elseCnt) {
		throw U.notExpected();
	}

	@Override
	public Tag<?> onClick(TagEventHandler<Tag<?>> handler) {
		throw U.notExpected();
	}

	@Override
	public Tag<?> onClick(Action... action) {
		throw U.notExpected();
	}

	@Override
	public Action doShow() {
		throw U.notExpected();
	}

	@Override
	public Action doHide() {
		throw U.notExpected();
	}

	@Override
	public Action doEnable() {
		throw U.notExpected();
	}

	@Override
	public Action doDisable() {
		throw U.notExpected();
	}

	@Override
	public Action doRemove() {
		throw U.notExpected();
	}

	@Override
	public Action doAdd(Object... tags) {
		throw U.notExpected();
	}

}
