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

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.rapidoid.html.HTML;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.TagProcessor;
import org.rapidoid.html.Tags;
import org.rapidoid.util.U;

public class TagContextImpl implements TagContext, Serializable {

	private static final long serialVersionUID = 4007586215607855031L;

	private final AtomicInteger counter = new AtomicInteger();

	private final Set<Tag<?>> changed = U.set();

	@Override
	public int newHnd() {
		return counter.incrementAndGet();
	}

	@Override
	public void changed(TagImpl<?> tag) {
		changed.add(tag.proxy());
	}

	@Override
	public Map<Integer, String> changes() {
		Map<Integer, String> content = U.map();

		for (Tag<?> tag : changed) {
			content.put(base(tag)._h, tag.toString());
		}

		return content;
	}

	@Override
	public void emit(Tag<?> root, final Map<Integer, Object> values, final int eventHnd, String event) {

		changed.clear();

		final AtomicReference<TagImpl<?>> ref = new AtomicReference<TagImpl<?>>(null);

		HTML.traverse(root, new TagProcessor<Tag<?>>() {
			@Override
			public void handle(Tag<?> tag) {

				TagImpl<Tag<?>> t = base(tag);

				Object val = values.get(t._h);
				if (val != null) {
					Tags.setValue(tag, val);
					values.remove(t._h);
				}

				if (t._h == eventHnd) {
					ref.set(t);
				}
			}
		});

		U.must(values.isEmpty(), "Missing input field(s): %s", values.keySet());

		TagImpl<?> tag = ref.get();

		if (tag != null) {
			tag.emit(event);
		} else {
			U.error("Cannot find tag!", "event", event, "_h", eventHnd);
			throw U.rte("Cannot find tag with _h = '%s'", eventHnd);
		}
	}

	private TagImpl<Tag<?>> base(Tag<?> tag) {
		return ((TagInternals) tag).base();
	}

}
