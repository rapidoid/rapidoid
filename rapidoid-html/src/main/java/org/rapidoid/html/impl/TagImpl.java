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

import org.rapidoid.html.Cmd;
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

	Cmd cmd;

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
	public TAG withChild(int index, Object child) {
		TAG _copy = copy();
		TagImpl<Tag<?>> impl = impl(_copy);

		impl.contents.set(index, child);

		return _copy;
	}

	@Override
	public TAG copy() {
		TAG _copy = TagProxy.create(clazz, name, contents.toArray());
		TagImpl<Tag<?>> impl = impl(_copy);

		impl.binding = binding;
		impl._h = _h;
		impl.cmd = cmd;
		impl.attrs.putAll(attrs);
		impl.battrs.addAll(battrs);

		return _copy;
	}

	@SuppressWarnings("unchecked")
	public TagImpl<Tag<?>> base() {
		return (TagImpl<Tag<?>>) this;
	}

	@Override
	public Object content() {
		return contents;
	}

	@Override
	public TAG content(Object... content) {
		TAG _copy = copy();
		TagImpl<Tag<?>> impl = impl(_copy);

		impl.contents.clear();
		impl.flatAndInsertContent(APPEND, content);

		return _copy;
	}

	@Override
	public TAG prepend(Object... content) {
		TAG _copy = copy();
		TagImpl<Tag<?>> impl = impl(_copy);

		impl.flatAndInsertContent(0, content);

		return _copy;
	}

	@Override
	public TAG append(Object... content) {
		TAG _copy = copy();
		TagImpl<Tag<?>> impl = impl(_copy);

		impl.flatAndInsertContent(APPEND, content);

		return _copy;
	}

	@Override
	public String attr(String attr) {
		return attrs.get(attr);
	}

	@Override
	public TAG attr(String attr, String value) {
		TAG _copy = copy();
		TagImpl<Tag<?>> impl = impl(_copy);

		impl.attrs.put(attr, value);

		return _copy;
	}

	@Override
	public boolean is(String attr) {
		return battrs.contains(attr);
	}

	@Override
	public TAG is(String attr, boolean value) {
		TAG _copy = copy();
		TagImpl<Tag<?>> impl = impl(_copy);

		if (value) {
			impl.battrs.add(attr);
		} else {
			impl.battrs.remove(attr);
		}

		return _copy;
	}

	public int hnd() {
		return _h;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> TAG bind(Var<T> var) {
		TAG _copy = (TAG) Tags.withValue(proxy, var.get());
		TagImpl<Tag<?>> impl = impl(_copy);

		impl.binding = (Var<Object>) var;

		return _copy;
	}

	@Override
	public TAG cmd(String cmd, Object... args) {
		TAG _copy = copy();
		TagImpl<Tag<?>> impl = impl(_copy);

		impl.cmd = new Cmd(cmd, args);

		return _copy;
	}

	private TagImpl<Tag<?>> impl(TAG tag) {
		return ((TagInternals) tag).base();
	}

}
