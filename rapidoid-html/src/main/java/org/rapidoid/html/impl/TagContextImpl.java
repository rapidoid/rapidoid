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

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.util.U;

public class TagContextImpl implements TagContext {

	private final AtomicInteger counter = new AtomicInteger();

	private final ConcurrentMap<String, TagImpl<?>> tags = U.concurrentMap();

	private final ConcurrentMap<String, Tag<?>> changed = U.concurrentMap();

	@Override
	public void emit(String hnd, String event) {
		TagImpl<?> tag = tags.get(hnd);
		if (tag != null) {
			tag.emit(event);
		} else {
			U.error("Cannot find tag!", "event", event, "_h", hnd);
			throw U.rte("Cannot find tag with _h = '%s'", hnd);
		}
	}

	@Override
	public String getNewId(TagImpl<?> tag) {
		String hnd = "_" + counter.incrementAndGet();
		tags.put(hnd, tag);
		return hnd;
	}

	@Override
	public Tag<?> get(String hnd) {
		TagImpl<?> tag = tags.get(hnd);

		if (tag == null) {
			U.error("Cannot find tag!", "_h", hnd);
			throw U.rte("Cannot find tag with _h = '%s'", hnd);
		}

		return tag.proxy();
	}

	@Override
	public Map<String, Tag<?>> changedTags() {
		return changed;
	}

	@Override
	public void changedContents(TagImpl<?> tag) {
		changed.putIfAbsent(tag._hnd, tag.proxy());
	}

	@Override
	public Map<String, String> changedContent() {
		Map<String, String> content = U.map();

		for (Entry<String, Tag<?>> e : changed.entrySet()) {
			content.put(e.getKey(), e.getValue().toString());
		}

		return content;
	}

}
