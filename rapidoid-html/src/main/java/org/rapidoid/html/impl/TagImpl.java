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
import java.util.List;
import java.util.Map;

import org.rapidoid.html.Action;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.TagEventHandler;
import org.rapidoid.util.U;

public class TagImpl<TAG extends Tag<?>> extends UndefinedTag<TAG> implements TagInternals {

	final Class<TAG> clazz;

	final String name;

	final List<Object> contents = U.list();

	final Map<String, String> attrs = U.map();

	final Set<String> battrs = U.set();

	final Map<String, TagEventHandler<TAG>> eventHandlers = U.map();

	String _hnd;

	TagContext ctx;

	private TAG proxy;

	private Object value;

	@SuppressWarnings("unchecked")
	public TagImpl(Class<TAG> clazz, String name, Object[] contentsAndHandlers) {
		this.clazz = clazz;
		this.name = name;

		List<Action> actions = U.list();

		for (Object x : contentsAndHandlers) {
			if (x instanceof TagEventHandler) {
				setHandler("click", (TagEventHandler<TAG>) x);
			} else if (x instanceof Action) {
				actions.add((Action) x);
			} else {
				contents.add(x);
			}
		}

		if (!actions.isEmpty()) {
			setHandler("click", actions.toArray(new Action[actions.size()]));
		}
	}

	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TagRenderer.get().str(this, 0, false, null, out);
		return out.toString();
	}

	public void setHandler(String event, TagEventHandler<TAG> handler) {
		if (handler != null) {
			eventHandlers.put(event, handler);
		} else {
			eventHandlers.remove(event);
		}
	}

	public void setHandler(String event, Action[] actions) {
		if (actions.length == 0) {
			setHandler(event, (TagEventHandler<TAG>) null);
		} else {
			setHandler(event, new ActionsHandler<TAG>(actions));
		}
	}

	public void setProxy(TAG proxy) {
		this.proxy = proxy;
	}

	public void setCtx(TagContext ctx) {
		this.ctx = ctx;
	}

	public void emit(String event) {
		TagEventHandler<TAG> handler = eventHandlers.get(event);
		if (handler != null) {
			U.notNull(proxy, "tag");
			handler.handle(proxy);
		} else {
			U.error("Cannot find event handler!", "event", event, "hnd", _hnd);
			throw U.rte("Cannot find event handler on tag with _h = '%s' for event = '%s'", _hnd, event);
		}
	}

	private void changedContents() {
		if (ctx != null) {
			ctx.changedContents(this);
		}
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
		return TagProxy.create(clazz, name, contents.toArray());
	}

	@SuppressWarnings("unchecked")
	public TagImpl<Tag<?>> base() {
		return (TagImpl<Tag<?>>) this;
	}

	@Override
	public void setHnd(String hnd) {
		_hnd = hnd;
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
		changedContents();
		contents.clear();
		append(content);
		return proxy();
	}

	@Override
	public TAG prepend(Object... content) {
		changedContents();
		int index = 0;
		for (Object obj : content) {
			contents.add(index++, obj);
		}
		return proxy();
	}

	@Override
	public TAG append(Object... content) {
		changedContents();
		for (Object obj : content) {
			contents.add(obj);
		}
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

	@Override
	public TAG classs(String classs) {
		return attr("class", classs);
	}

	@Override
	public String classs() {
		return attr("class");
	}

	public void value(Object value) {
		this.value = value;
	}

	public Object value() {
		return value;
	}

}
