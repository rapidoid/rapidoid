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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rapidoid.html.Action;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.TagEventHandler;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;

public class TagImpl<TAG extends Tag<?>> extends UndefinedTag<TAG> implements TagInternals, Serializable {

	private static final long serialVersionUID = -8137919597555179907L;

	final Class<TAG> clazz;

	final String name;

	final List<Object> contents = U.list();

	final Map<String, String> attrs = U.map();

	final Set<String> battrs = U.set();

	Var<Object> contentBinding;

	final Map<String, Var<?>> bindings = U.map();

	final Map<String, TagEventHandler<TAG>> eventHandlers = U.map();

	int _h;

	TagContext ctx;

	private TAG proxy;

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
	public String tagKind() {
		return name;
	}

	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TagRenderer.get().str(ctx, this, 0, false, null, out);
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
			U.error("Cannot find event handler!", "event", event, "hnd", _h);
			throw U.rte("Cannot find event handler on tag with _h = '%s' for event = '%s'", _h, event);
		}
	}

	private void changed() {
		if (ctx != null) {
			ctx.changed(this);
		}
	}

	private void changedContent() {
		if (contentBinding != null) {
			contentBinding.set(contents.size() == 1 ? contents.get(0) : contents);
		}
		changed();
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
		changedContent();
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
		for (Object obj : content) {
			contents.add(obj);
		}
		changedContent();
		return proxy();
	}

	@Override
	public TAG prepend(Object... content) {
		int index = 0;
		for (Object obj : content) {
			contents.add(index++, obj);
		}
		changedContent();
		return proxy();
	}

	@Override
	public TAG append(Object... content) {
		for (Object obj : content) {
			contents.add(obj);
		}
		changedContent();
		return proxy();
	}

	@Override
	public String attr(String attr) {
		return attrs.get(attr);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TAG attr(String attr, String value) {
		String prev = attrs.put(attr, value);

		if (!U.eq(prev, value)) {
			Var<String> var = (Var<String>) bindings.get(attr);
			if (var != null) {
				var.set(value);
			}
			changed();
		}

		return proxy();
	}

	@Override
	public boolean is(String attr) {
		return battrs.contains(attr);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TAG is(String attr, boolean value) {
		boolean changed;

		if (value) {
			changed = battrs.add(attr);
		} else {
			changed = battrs.remove(attr);
		}

		if (changed) {
			Var<Boolean> var = (Var<Boolean>) bindings.get(attr);
			if (var != null) {
				var.set(value);
			}
			changed();
		}

		return proxy();
	}

	@Override
	public TAG bind(String attr, Var<String> var) {
		U.must(!bindings.containsKey(attr), "The attribute '%s' of tag '%s' is already bound to a var!", attr, name);
		bindings.put(attr, var);
		attr(attr, var.get());
		return proxy();
	}

	public TAG bindIs(String attr, Var<Boolean> var) {
		U.must(!bindings.containsKey(attr), "The attribute '%s' of tag '%s' is already bound to a var!", attr, name);
		bindings.put(attr, var);
		is(attr, var.get());
		return proxy();
	}

	@Override
	public TAG unbind(String attr) {
		U.must(bindings.containsKey(attr), "The attribute '%s' of tag '%s' is not bound to a var!", attr, name);
		bindings.remove(attr);
		return proxy();
	}

	@Override
	public TAG bindContent(Var<Object> var) {
		U.must(contentBinding == null, "The content of tag '%s' is already bound to a var!", name);
		contentBinding = var;
		content(var.get());
		return proxy();
	}

	@Override
	public TAG unbindContent(String attr) {
		U.must(contentBinding != null, "The content of tag '%s' is not bound to a var!", name);
		contentBinding = null;
		return proxy();
	}

}
