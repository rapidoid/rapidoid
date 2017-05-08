package org.rapidoid.html.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagProcessor;

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
public class UndefinedTag extends RapidoidThing implements Tag {

	@Override
	public Object contents() {
		throw Err.notExpected();
	}

	@Override
	public Tag contents(Object... content) {
		throw Err.notExpected();
	}

	@Override
	public Tag append(Object... content) {
		throw Err.notExpected();
	}

	@Override
	public Tag prepend(Object... content) {
		throw Err.notExpected();
	}

	@Override
	public String id() {
		throw Err.notExpected();
	}

	@Override
	public Tag id(String id) {
		throw Err.notExpected();
	}

	@Override
	public String role() {
		throw Err.notExpected();
	}

	@Override
	public Tag role(String role) {
		throw Err.notExpected();
	}

	@Override
	public String style() {
		throw Err.notExpected();
	}

	@Override
	public Tag style(String css) {
		throw Err.notExpected();
	}

	@Override
	public boolean hidden() {
		throw Err.notExpected();
	}

	@Override
	public Tag hidden(boolean hidden) {
		throw Err.notExpected();
	}

	@Override
	public boolean disabled() {
		throw Err.notExpected();
	}

	@Override
	public Tag disabled(boolean value) {
		throw Err.notExpected();
	}

	@Override
	public Tag attr(String attr, String value) {
		throw Err.notExpected();
	}

	@Override
	public Tag attr(String attr, int value) {
		throw Err.notExpected();
	}

	@Override
	public Tag data(String dataAttr, String value) {
		throw Err.notExpected();
	}

	@Override
	public Tag data(String dataAttr, int value) {
		throw Err.notExpected();
	}

	@Override
	public Tag ng(String ngAttr, String value) {
		throw Err.notExpected();
	}

	@Override
	public String attr(String attr) {
		throw Err.notExpected();
	}

	@Override
	public Tag class_(String classs) {
		throw Err.notExpected();
	}

	@Override
	public String class_() {
		throw Err.notExpected();
	}

	@Override
	public int size() {
		throw Err.notExpected();
	}

	@Override
	public boolean isEmpty() {
		throw Err.notExpected();
	}

	@Override
	public Object child(int index) {
		throw Err.notExpected();
	}

	@Override
	public Tag withChild(int index, Object replace) {
		throw Err.notExpected();
	}

	@Override
	public Tag copy() {
		throw Err.notExpected();
	}

	@Override
	public boolean is(String attr) {
		throw Err.notExpected();
	}

	@Override
	public Tag is(String attr, boolean value) {
		throw Err.notExpected();
	}

	public void traverse(TagProcessor<Tag> processor) {
		throw Err.notExpected();
	}

	@Override
	public String tagKind() {
		throw Err.notExpected();
	}

	@Override
	public String onclick() {
		throw Err.notExpected();
	}

	@Override
	public Tag onclick(String onclick) {
		throw Err.notExpected();
	}

	@Override
	public Tag cmd(String cmd, Object... args) {
		throw Err.notExpected();
	}

	@Override
	public Tag extra(String attr, Object value) {
		throw Err.notExpected();
	}

}
