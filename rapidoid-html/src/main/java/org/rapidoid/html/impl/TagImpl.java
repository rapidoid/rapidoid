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

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tags;
import org.rapidoid.reactive.Var;
import org.rapidoid.util.U;

public class TagImpl<TAG extends Tag<?>> extends UndefinedTag<TAG> implements TagInternals, Serializable {

	private static final long serialVersionUID = -8137919597555179907L;

	private static final int APPEND = Integer.MAX_VALUE;

	final Class<TAG> clazz;

	final String name;

	final List<Object> contents = U.list();

	final Map<String, String> attrs = U.map();

	final Set<String> battrs = U.set();

	int _h = -1;

	TagContext ctx;

	TAG proxy;

	Var<Object> binding;

	String cmd;

	public TagImpl(Class<TAG> clazz, String name, Object[] contents) {
		this.clazz = clazz;
		this.name = name;

		flatAndInsertContent(APPEND, contents);
	}

	private void flatAndInsertContent(int index, Object item) {
		if (item instanceof Object[]) {
			Object[] arr = (Object[]) item;
			for (Object obj : arr) {
				flatAndInsertContent(index, obj);
			}
		} else if (item instanceof Collection<?>) {
			Collection<?> coll = (Collection<?>) item;
			for (Object obj : coll) {
				flatAndInsertContent(index, obj);
			}
		} else if (item != null) {
			if (index == APPEND) {
				contents.add(item);
			} else {
				contents.add(index, item);
			}
		}
	}

	@Override
	public String tagKind() {
		return name;
	}

	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TagRenderer.get().str(ctx, this, 0, false, null, out);
		return out.toString();
	}

	public void setProxy(TAG proxy) {
		this.proxy = proxy;
	}

	public void setCtx(TagContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public int size() {
		return contents.size();
	}

	@Override
	public Object child(int index) {
		return contents.get(index);
	}

	@Override
	public void setChild(int index, Object child) {
		contents.set(index, child);
	}

	@Override
	public TAG copy() {
		TAG copy = TagProxy.create(clazz, name, contents.toArray());

		TagInternals tagi = (TagInternals) copy;
		tagi.base().attrs.putAll(attrs);
		tagi.base().battrs.addAll(battrs);

		return copy;
	}

	@SuppressWarnings("unchecked")
	public TagImpl<Tag<?>> base() {
		return (TagImpl<Tag<?>>) this;
	}

	@Override
	public Object content() {
		return contents;
	}

	public TAG proxy() {
		return proxy;
	}

	@Override
	public TAG content(Object... content) {
		contents.clear();
		flatAndInsertContent(APPEND, content);
		return proxy();
	}

	@Override
	public TAG prepend(Object... content) {
		flatAndInsertContent(0, content);
		return proxy();
	}

	@Override
	public TAG append(Object... content) {
		flatAndInsertContent(APPEND, content);
		return proxy();
	}

	@Override
	public String attr(String attr) {
		return attrs.get(attr);
	}

	@Override
	public TAG attr(String attr, String value) {
		attrs.put(attr, value);
		return proxy();
	}

	@Override
	public boolean is(String attr) {
		return battrs.contains(attr);
	}

	@Override
	public TAG is(String attr, boolean value) {
		if (value) {
			battrs.add(attr);
		} else {
			battrs.remove(attr);
		}

		return proxy();
	}

	public int hnd() {
		return _h;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> TAG bind(Var<T> var) {
		Tags.setValue(proxy(), var.get());
		U.must(binding == null);
		binding = (Var<Object>) var;
		return proxy();
	}

	@Override
	public TAG cmd(String cmd) {
		this.cmd = cmd;
		return proxy();
	}

}
