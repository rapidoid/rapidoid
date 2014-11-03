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

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.rapidoid.pages.Tag;
import org.rapidoid.util.U;

public class GuiContext {

	private final AtomicInteger counter = new AtomicInteger();

	private final ConcurrentMap<String, TagData<?>> tags = U.concurrentMap();

	private final ConcurrentMap<String, Tag<?>> changed = U.concurrentMap();

	public String getNewId(TagData<?> tag) {
		String hnd = "_" + counter.incrementAndGet();
		tags.put(hnd, tag);
		return hnd;
	}

	public void emit(String event, String hnd) {
		TagData<?> tag = tags.get(hnd);
		if (tag != null) {
			tag.emit(event);
		} else {
			U.warn("Cannot find tag!", "event", event, "hnd", hnd);
		}
	}

	public Tag<?> get(String hnd) {
		TagData<?> tag = tags.get(hnd);

		if (tag == null) {
			U.warn("Cannot find tag!", "hnd", hnd);
		}

		return tag.tag;
	}

	public Map<String, Tag<?>> changedTags() {
		return changed;
	}

	public void changedContents(TagData<?> tagData) {
		changed.putIfAbsent(tagData._hnd, tagData.tag);
	}

	public Map<String, String> changedContent() {
		Map<String, String> content = U.map();

		for (Entry<String, Tag<?>> e : changed.entrySet()) {
			content.put(e.getKey(), e.getValue().toString());
		}

		return content;
	}

}
