package org.rapidoid.gui;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.TableTag;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Mapper;

import java.util.Map;
import java.util.Map.Entry;

/*
 * #%L
 * rapidoid-gui
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
@Since("2.4.0")
public class KVGrid extends AbstractWidget<KVGrid> {

	private final String[] headers = {"Key", "Value"};

	private Mapper<Object, Object> keyView = null;

	private Mapper<Object, Object> valueView = null;

	private Map<?, ?> map;

	private boolean headless;

	@Override
	protected Object render() {
		if (map.isEmpty()) {
			return GUI.NOTHING;
		}

		TableTag tbl = headless ? GUI.table_() : GUI.table_(tr(th(headers[0]), th(headers[1])));

		for (Entry<?, ?> e : map.entrySet()) {
			Object key = e.getKey();
			if (keyView != null) {
				key = Lmbd.eval(keyView, key);
			} else {
				key = GUI.display(key);
			}

			Object val = e.getValue();
			if (valueView != null) {
				val = Lmbd.eval(valueView, val);
			} else {
				val = GUI.display(val);
			}

			Tag tr = val != null ? tr(td(key), td(val)) : tr(td(key).colspan("2"));
			tbl = tbl.append(tr);
		}

		return tbl;
	}

	public Map<?, ?> map() {
		return map;
	}

	public KVGrid map(Map<?, ?> map) {
		this.map = map;
		return this;
	}

	public String[] headers() {
		return headers;
	}

	public KVGrid headers(String keyHeader, String valueHeader) {
		this.headers[0] = keyHeader;
		this.headers[1] = valueHeader;
		return this;
	}

	public Mapper<Object, Object> keyView() {
		return keyView;
	}

	public Mapper<Object, Object> valueView() {
		return valueView;
	}

	@SuppressWarnings("unchecked")
	public <FROM, TO> KVGrid keyView(Mapper<FROM, TO> keyView) {
		this.keyView = (Mapper<Object, Object>) keyView;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <FROM, TO> KVGrid valueView(Mapper<FROM, TO> valueView) {
		this.valueView = (Mapper<Object, Object>) valueView;
		return this;
	}

	public KVGrid headless(boolean headless) {
		this.headless = headless;
		return this;
	}

	public boolean headless() {
		return headless;
	}

}
