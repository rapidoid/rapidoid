package org.rapidoid.html.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.AnyObj;
import org.rapidoid.commons.Err;
import org.rapidoid.html.Cmd;
import org.rapidoid.html.Tag;
import org.rapidoid.u.U;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class TagImpl extends UndefinedTag implements TagInternals, Serializable {

	private static final long serialVersionUID = -8137919597555179907L;

	private static final int APPEND = Integer.MAX_VALUE;

	final Class<?> clazz;

	final String name;

	final List<Object> contents = U.list();

	final Map<String, String> attrs = U.map();

	final Map<String, Object> extras = U.map();

	final Set<String> battrs = U.set();

	String _h = null;

	Tag proxy;

	Cmd cmd;

	public TagImpl(Class<?> clazz, String name, Object[] contents) {
		this.clazz = clazz;
		this.name = name;
		AnyObj.flatInsertInto(this.contents, APPEND, contents);
	}

	@Override
	public String tagKind() {
		return name;
	}

	@Override
	public String toString() {
		throw Err.notExpected();
	}

	public void setProxy(Tag proxy) {
		this.proxy = proxy;
	}

	@Override
	public int size() {
		return contents.size();
	}

	@Override
	public boolean isEmpty() {
		return contents.isEmpty();
	}

	@Override
	public Object child(int index) {
		return contents.get(index);
	}

	@Override
	public Tag withChild(int index, Object child) {
		Tag _copy = copy();
		TagImpl impl = impl(_copy);

		impl.contents.set(index, child);

		return _copy;
	}

	@Override
	public Tag copy() {
		Tag _copy = TagProxy.create(clazz, name, contents.toArray());
		TagImpl impl = impl(_copy);

		impl._h = _h;
		impl.cmd = cmd;
		impl.attrs.putAll(attrs);
		impl.battrs.addAll(battrs);
		impl.extras.putAll(extras);

		return _copy;
	}

	public TagImpl base() {
		return (TagImpl) this;
	}

	@Override
	public Object contents() {
		return contents;
	}

	@Override
	public Tag contents(Object... content) {
		Tag _copy = copy();
		TagImpl impl = impl(_copy);

		impl.contents.clear();
		AnyObj.flatInsertInto(impl.contents, APPEND, content);

		return _copy;
	}

	@Override
	public Tag prepend(Object... content) {
		Tag _copy = copy();
		TagImpl impl = impl(_copy);

		AnyObj.flatInsertInto(impl.contents, 0, content);

		return _copy;
	}

	@Override
	public Tag append(Object... content) {
		Tag _copy = copy();
		TagImpl impl = impl(_copy);

		AnyObj.flatInsertInto(impl.contents, APPEND, content);

		return _copy;
	}

	@Override
	public String attr(String attr) {
		return attrs.get(attr);
	}

	@Override
	public Tag attr(String attr, String value) {
		Tag _copy = copy();
		TagImpl impl = impl(_copy);

		impl.attrs.put(attr, value);

		return _copy;
	}

	@Override
	public Tag attr(String attr, int value) {
		return attr(attr, value + "");
	}

	@Override
	public Tag data(String dataAttr, String value) {
		return attr("data-" + dataAttr, value);
	}

	@Override
	public Tag data(String dataAttr, int value) {
		return data(dataAttr, value + "");
	}

	@Override
	public Tag ng(String ngAttr, String value) {
		return attr("ng-" + ngAttr, value);
	}

	@Override
	public boolean is(String attr) {
		return battrs.contains(attr);
	}

	@Override
	public Tag is(String attr, boolean value) {
		Tag _copy = copy();
		TagImpl impl = impl(_copy);

		if (value) {
			impl.battrs.add(attr);
		} else {
			impl.battrs.remove(attr);
		}

		return _copy;
	}

	@Override
	public Tag extra(String attr, Object value) {
		Tag _copy = copy();
		TagImpl impl = impl(_copy);

		impl.extras.put(attr, value);

		return _copy;
	}

	public String hnd() {
		return _h;
	}

	@Override
	public Tag cmd(String cmd, Object... args) {
		Tag _copy = copy();
		TagImpl impl = impl(_copy);

		impl.cmd = cmd != null ? new Cmd(cmd, args) : null;

		return _copy;
	}

	private TagImpl impl(Tag tag) {
		return ((TagInternals) tag).base();
	}

}
