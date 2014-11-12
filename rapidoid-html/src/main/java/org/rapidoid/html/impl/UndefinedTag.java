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
import org.rapidoid.html.TagProcessor;
import org.rapidoid.util.U;

public class UndefinedTag<TAG extends Tag<?>> implements Tag<TAG> {

	private static final long serialVersionUID = -7982241929117711564L;

	@Override
	public Object content() {
		throw U.notExpected();
	}

	@Override
	public TAG content(Object... content) {
		throw U.notExpected();
	}

	@Override
	public TAG append(Object... content) {
		throw U.notExpected();
	}

	@Override
	public TAG prepend(Object... content) {
		throw U.notExpected();
	}

	@Override
	public String id() {
		throw U.notExpected();
	}

	@Override
	public TAG id(String id) {
		throw U.notExpected();
	}

	@Override
	public String role() {
		throw U.notExpected();
	}

	@Override
	public TAG role(String role) {
		throw U.notExpected();
	}

	@Override
	public String css() {
		throw U.notExpected();
	}

	@Override
	public TAG css(String css) {
		throw U.notExpected();
	}

	@Override
	public boolean hidden() {
		throw U.notExpected();
	}

	@Override
	public TAG hidden(boolean hidden) {
		throw U.notExpected();
	}

	@Override
	public boolean enabled() {
		throw U.notExpected();
	}

	@Override
	public TAG enabled(boolean value) {
		throw U.notExpected();
	}

	@Override
	public TAG attr(String attr, String value) {
		throw U.notExpected();
	}

	@Override
	public String attr(String attr) {
		throw U.notExpected();
	}

	@Override
	public TAG class_(String classs) {
		throw U.notExpected();
	}

	@Override
	public String class_() {
		throw U.notExpected();
	}

	@Override
	public TAG onClick(TagEventHandler<TAG> handler) {
		throw U.notExpected();
	}

	@Override
	public TAG onClick(Action... action) {
		throw U.notExpected();
	}

	@Override
	public int size() {
		throw U.notExpected();
	}

	@Override
	public Object child(int index) {
		throw U.notExpected();
	}

	@Override
	public void setChild(int index, Object replace) {
		throw U.notExpected();
	}

	@Override
	public TAG copy() {
		throw U.notExpected();
	}

	@Override
	public boolean is(String attr) {
		throw U.notExpected();
	}

	@Override
	public TAG is(String attr, boolean value) {
		throw U.notExpected();
	}

	public void traverse(TagProcessor<Tag<?>> processor) {
		throw U.notExpected();
	}

	@Override
	public String tagKind() {
		throw U.notExpected();
	}

}
