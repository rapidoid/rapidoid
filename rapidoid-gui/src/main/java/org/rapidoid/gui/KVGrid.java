package org.rapidoid.gui;

/*
 * #%L
 * rapidoid-gui
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

import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.html.tag.TableTag;
import org.rapidoid.lambda.Mapper;

@Authors("Nikolche Mihajlovski")
@Since("2.4.0")
public class KVGrid extends AbstractWidget {

	private final String[] headers = { "Key", "Value" };

	@SuppressWarnings("rawtypes")
	private final Mapper[] view = { null, null };

	private Map<?, ?> map;

	@Override
	protected Object render() {
		TableTag tbl = table_(tr(th(headers[0]), th(headers[1])));

		for (Entry<?, ?> e : map.entrySet()) {
			tbl = tbl.append(tr(td(e.getKey()), td(e.getValue())));
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

}
