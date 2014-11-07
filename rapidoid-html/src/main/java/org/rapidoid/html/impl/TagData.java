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

import java.util.List;
import java.util.Map;

import org.rapidoid.html.Action;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagEventHandler;
import org.rapidoid.util.U;

@SuppressWarnings("unchecked")
public class TagData<TAG extends Tag<?>> {

	final Class<TAG> clazz;

	final String name;

	final List<Object> contents = U.list();

	final Map<String, Object> attrs = U.map();

	final Map<String, TagEventHandler<TAG>> eventHandlers = U.map();

	String _hnd;

	TAG tag;

	TagContext ctx;

	public TagData(Class<TAG> clazz, String name, Object[] contentsAndHandlers) {
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
		return TagRenderer.str(this, 0, false);
	}

	public void set(String attr, Object value) {
		attrs.put(attr, value);
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

	public void setTag(TAG tag) {
		this.tag = tag;
	}

	public void setCtx(TagContext ctx) {
		this.ctx = ctx;
	}

	public void emit(String event) {
		TagEventHandler<TAG> handler = eventHandlers.get(event);
		if (handler != null) {
			U.notNull(tag, "tag");
			handler.handle(tag);
		} else {
			U.error("Cannot find event handler!", "event", event, "hnd", _hnd);
			throw U.rte("Cannot find event handler on tag with _h = '%s' for event = '%s'", _hnd, event);
		}
	}

	public void content(Object[] objects) {
		changedContents();
		contents.clear();
		append(objects);
	}

	public void prepend(Object[] objects) {
		changedContents();
		int index = 0;
		for (Object obj : objects) {
			contents.add(index++, obj);
		}
	}

	public void append(Object[] objects) {
		changedContents();
		for (Object obj : objects) {
			contents.add(obj);
		}
	}

	private void changedContents() {
		if (ctx != null) {
			ctx.changedContents(this);
		}
	}

	public void setHnd(String hnd) {
		_hnd = hnd;
	}

}
