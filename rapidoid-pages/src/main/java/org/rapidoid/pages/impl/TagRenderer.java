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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringEscapeUtils;
import org.rapidoid.pages.Tag;
import org.rapidoid.pages.Var;
import org.rapidoid.util.U;

public class TagRenderer {

	public static String str(Object content) {
		return str(content, 0, false);
	}

	public static String str(Object content, int level, boolean inline) {

		if (content instanceof Tag) {
			Tag<?> tag = (Tag<?>) content;
			return tag.str(level);
		} else if (content instanceof Object[]) {
			return join((Object[]) content, level, inline);
		} else if (content instanceof Collection<?>) {
			return join((Collection<?>) content, level, inline);
		}

		String indent = !inline ? U.mul("  ", level) : "";
		return indent + String.valueOf(content);
	}

	private static String join(Collection<?> items, int level, boolean inline) {
		StringBuilder sb = new StringBuilder();

		for (Object item : items) {
			if (!inline) {
				sb.append("\n");
			}
			sb.append(str(item, level + 1, inline));
		}

		return sb.toString();
	}

	private static String join(Object[] items, int level, boolean inline) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < items.length; i++) {
			if (!inline) {
				sb.append("\n");
			}
			sb.append(str(items[i], level + 1, inline));
		}

		return sb.toString();
	}

	public static String str(TagData<?> tag, int level, boolean inline) {

		String name = escape(tag.name);
		List<Object> contents = tag.contents;

		StringBuilder sb = new StringBuilder();

		for (Entry<String, List<Object>> e : tag.attrs.entrySet()) {
			sb.append(" ");
			sb.append(escape(e.getKey()));
			sb.append("=\"");
			sb.append(escape(U.join(" ", e.getValue())));
			sb.append("\"");
		}

		for (String event : tag.eventHandlers.keySet()) {
			sb.append(" on");
			sb.append(event);
			sb.append("=\"");
			sb.append("_emit(this, '");
			sb.append(event);
			sb.append("')");
			sb.append("\"");
		}

		String attrib = sb.toString();

		String indent = !inline ? U.mul("  ", level) : "";

		if (contents == null || contents.isEmpty()) {
			return U.format("%s<%s%s></%s>", indent, name, attrib, name);
		}

		if (inline || isSimpleOrHasSimpleContent(contents)) {
			String content = str(contents, level + 1, true);
			return U.format("%s<%s%s>%s</%s>", indent, name, attrib, content, name);
		}

		sb = new StringBuilder();

		if (contents != null) {
			if (contents.size() < 2) {
				sb.append(str(contents, level, inline));
			} else {
				sb.append(str(contents, level, inline));
			}
		}

		String inside = sb.toString();
		return U.format("%s<%s%s>%s\n%s</%s>", indent, name, attrib, inside, indent, name);
	}

	private static String escape(String s) {
		return StringEscapeUtils.escapeHtml4(s);
	}

	private static boolean isSimpleOrHasSimpleContent(Object content) {
		if (isSimpleContent(content)) {
			return true;
		}

		if (content instanceof Object[]) {
			return hasSimpleContent((Object[]) content);
		}

		if (content instanceof Collection<?>) {
			return hasSimpleContent((Collection<?>) content);
		}

		return false;
	}

	private static boolean isSimpleContent(Object content) {
		if (content instanceof String || content instanceof Number || content instanceof Boolean
				|| content instanceof Date) {
			return true;
		}

		if (content instanceof Var<?>) {
			Var<?> var = (Var<?>) content;
			return isSimpleContent(var.get());
		}

		return false;
	}

	private static boolean hasSimpleContent(Collection<?> content) {
		for (Object cnt : content) {
			if (isSimpleContent(cnt)) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasSimpleContent(Object[] content) {
		for (Object cnt : content) {
			if (isSimpleContent(cnt)) {
				return true;
			}
		}
		return false;
	}

}
