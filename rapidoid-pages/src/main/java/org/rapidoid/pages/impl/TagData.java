package org.rapidoid.pages.impl;

/*
 * #%L
 * rapidoid-pages
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

import org.rapidoid.pages.Action;
import org.rapidoid.pages.Handler;
import org.rapidoid.pages.Tag;
import org.rapidoid.util.U;

@SuppressWarnings("unchecked")
public class TagData<TAG extends Tag<?>> {

	final GuiContext ctx;

	final Class<TAG> clazz;

	final String name;

	final List<Object> contents = U.list();

	final Map<String, List<Object>> attrs = U.map();

	final Map<String, Handler<TAG>> eventHandlers = U.map();

	private String _hnd;

	private TAG tag;

	public TagData(GuiContext ctx, Class<TAG> clazz, String name, Object[] contentsAndHandlers) {
		this.ctx = ctx;
		this.clazz = clazz;
		this.name = name;

		List<Action> actions = U.list();

		for (Object x : contentsAndHandlers) {
			if (x instanceof Handler) {
				setHandler("click", (Handler<TAG>) x);
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

	public void set(String key, Object value) {
		List<Object> list = attrs.get(key);
		if (list == null) {
			list = U.list(value);
			attrs.put(key, list);
		} else {
			list.add(value);
		}
	}

	public void setHandler(String event, Handler<TAG> handler) {
		getEventHandle();

		if (handler != null) {
			eventHandlers.put(event, handler);
		} else {
			eventHandlers.remove(event);
		}
	}

	public void setHandler(String event, Action[] actions) {
		if (actions.length == 0) {
			setHandler(event, (Handler<TAG>) null);
		} else {
			setHandler(event, new ActionsHandler<TAG>(actions));
		}
	}

	public void setTag(TAG tag) {
		this.tag = tag;
	}

	private synchronized void getEventHandle() {
		if (_hnd == null) {
			_hnd = ctx.getNewId((TagData<?>) this);
		}
	}

	public void emit(String event) {
		Handler<TAG> handler = eventHandlers.get(event);
		if (handler != null) {
			U.notNull(tag, "tag");
			handler.handle(tag);
		} else {
			U.warn("Cannot find event handler!", "event", event, "hnd", _hnd);
		}
	}

}
