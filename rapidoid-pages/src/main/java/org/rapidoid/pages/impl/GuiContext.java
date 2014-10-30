package org.rapidoid.pages.impl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.rapidoid.util.U;

public class GuiContext {

	private AtomicInteger counter = new AtomicInteger();

	private Map<String, TagData<?>> tags = U.map();

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

}
