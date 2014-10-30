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

	public static String toString(Object content) {
		return str(content, 0);
	}

	public static String str(Object content, int level) {

		if (content instanceof Tag) {
			Tag<?> tag = (Tag<?>) content;
			return tag.str(level);
		} else if (content instanceof Object[]) {
			return join((Object[]) content, level);
		} else if (content instanceof Collection<?>) {
			return join((Collection<?>) content, level);
		}

		return U.mul("  ", level) + String.valueOf(content);
	}

	private static String join(Collection<?> items, int level) {
		StringBuilder sb = new StringBuilder();

		for (Object item : items) {
			sb.append("\n");
			sb.append(str(item, level + 1));
		}

		return sb.toString();
	}

	private static String join(Object[] items, int level) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < items.length; i++) {
			sb.append("\n");
			sb.append(str(items[i], level + 1));
		}

		return sb.toString();
	}

	public static String str(TagData<?> tag, int level) {
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

		String attrib = sb.toString();

		String indent = U.mul("  ", level);

		if (contents == null || contents.isEmpty()) {
			return U.format("%s<%s%s></%s>", indent, name, attrib, name);
		}

		if (contents.size() == 1) {
			Object item = contents.get(0);
			if (isSimpleContent(item)) {
				String content = escape(str(item, 0));
				return U.format("%s<%s%s>%s</%s>", indent, name, attrib, content, name);
			}
		}

		sb = new StringBuilder();

		if (contents != null) {
			if (contents.size() < 2) {
				sb.append(str(contents, level));
			} else {
				sb.append(str(contents, level));
			}
			// for (Object cnt : contents) {
			// sb.append("\n");
			// sb.append(U.mul("  ", level));
			// sb.append(str((cnt));
			// }
		}

		String inside = sb.toString();
		return U.format("%s<%s%s>%s\n%s</%s>", indent, name, attrib, inside, indent, name);
	}

	private static String escape(String s) {
		return StringEscapeUtils.escapeHtml4(s);
	}

	private static boolean isSimpleContent(Object content) {
		return content instanceof String || content instanceof Number || content instanceof Boolean
				|| content instanceof Date
				|| ((content instanceof Var<?>) && isSimpleContent(((Var<?>) content).get()));
	}

}
