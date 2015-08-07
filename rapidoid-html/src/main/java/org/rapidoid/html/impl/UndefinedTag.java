package org.rapidoid.html.impl;

/*
 * #%L
 * rapidoid-html
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagProcessor;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class UndefinedTag implements Tag {

	@Override
	public Object contents() {
		throw U.notExpected();
	}

	@Override
	public Tag contents(Object... content) {
		throw U.notExpected();
	}

	@Override
	public Tag append(Object... content) {
		throw U.notExpected();
	}

	@Override
	public Tag prepend(Object... content) {
		throw U.notExpected();
	}

	@Override
	public String id() {
		throw U.notExpected();
	}

	@Override
	public Tag id(String id) {
		throw U.notExpected();
	}

	@Override
	public String role() {
		throw U.notExpected();
	}

	@Override
	public Tag role(String role) {
		throw U.notExpected();
	}

	@Override
	public String style() {
		throw U.notExpected();
	}

	@Override
	public Tag style(String css) {
		throw U.notExpected();
	}

	@Override
	public boolean hidden() {
		throw U.notExpected();
	}

	@Override
	public Tag hidden(boolean hidden) {
		throw U.notExpected();
	}

	@Override
	public boolean disabled() {
		throw U.notExpected();
	}

	@Override
	public Tag disabled(boolean value) {
		throw U.notExpected();
	}

	@Override
	public Tag attr(String attr, String value) {
		throw U.notExpected();
	}

	@Override
	public String attr(String attr) {
		throw U.notExpected();
	}

	@Override
	public Tag class_(String classs) {
		throw U.notExpected();
	}

	@Override
	public String class_() {
		throw U.notExpected();
	}

	@Override
	public int size() {
		throw U.notExpected();
	}

	@Override
	public boolean isEmpty() {
		throw U.notExpected();
	}

	@Override
	public Object child(int index) {
		throw U.notExpected();
	}

	@Override
	public Tag withChild(int index, Object replace) {
		throw U.notExpected();
	}

	@Override
	public Tag copy() {
		throw U.notExpected();
	}

	@Override
	public boolean is(String attr) {
		throw U.notExpected();
	}

	@Override
	public Tag is(String attr, boolean value) {
		throw U.notExpected();
	}

	public void traverse(TagProcessor<Tag> processor) {
		throw U.notExpected();
	}

	@Override
	public String tagKind() {
		throw U.notExpected();
	}

	@Override
	public String onclick() {
		throw U.notExpected();
	}

	@Override
	public Tag onclick(String onclick) {
		throw U.notExpected();
	}

	@Override
	public <T> Tag var(Var<T> var) {
		throw U.notExpected();
	}

	@Override
	public Tag cmd(String cmd, Object... args) {
		throw U.notExpected();
	}

	@Override
	public Tag navigate(String cmd, Object... args) {
		throw U.notExpected();
	}

	@Override
	public Tag extra(String attr, Object value) {
		throw U.notExpected();
	}

}
