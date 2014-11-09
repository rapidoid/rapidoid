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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.rapidoid.html.CustomTag;
import org.rapidoid.html.HTML;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagWidget;
import org.rapidoid.html.Var;
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;

public class TagRenderer {

	private static final byte[] EMIT_CLOSE = "')".getBytes();
	private static final byte[] INDENT = "  ".getBytes();
	private static final byte[] EMIT = "_emit(this, '".getBytes();
	private static final byte[] _H = " _h=\"".getBytes();
	private static final byte[] ON = " on".getBytes();
	private static final byte[] EQ_DQUOTES = "=\"".getBytes();
	private static final byte[] LT = "<".getBytes();
	private static final byte[] DQUOTES = "\"".getBytes();
	private static final byte[] LT_SLASH = "</".getBytes();
	private static final byte[] GT = ">".getBytes();

	protected static final TagRenderer INSTANCE = new TagRenderer();

	public static TagRenderer get() {
		return INSTANCE;
	}

	public String toHTML(Object content, Object extra) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		str(content, extra, out);
		return out.toString();
	}

	public void str(Object content, Object extra, OutputStream out) {
		str(content, 0, false, extra, out);
	}

	public void str(Object content, int level, boolean inline, Object extra, OutputStream out) {

		if (content instanceof Tag) {
			Tag<?> tag = (Tag<?>) content;
			TagInternals tagi = (TagInternals) tag;
			str(tagi.base(), level, inline, extra, out);
			return;
		} else if (content instanceof TagWidget) {
			TagWidget widget = (TagWidget) content;
			str(widget.content(), level, inline, extra, out);
			return;
		} else if (content instanceof Object[]) {
			join((Object[]) content, level, inline, extra, out);
			return;
		} else if (content instanceof Collection<?>) {
			join((Collection<?>) content, level, inline, extra, out);
			return;
		}

		indent(out, level, inline);
		write(out, HTML.escape(String.valueOf(content)));
	}

	protected void join(Collection<?> items, int level, boolean inline, Object extra, OutputStream out) {
		for (Object item : items) {
			if (!inline) {
				write(out, Constants.CR_LF);
			}
			str(item, level + 1, inline, extra, out);
		}
	}

	protected void join(Object[] items, int level, boolean inline, Object extra, OutputStream out) {
		for (int i = 0; i < items.length; i++) {
			if (!inline) {
				write(out, Constants.CR_LF);
			}
			str(items[i], level + 1, inline, extra, out);
		}
	}

	public void str(TagImpl<?> tag, int level, boolean inline, Object extra, OutputStream out) {

		String name = HTML.escape(tag.name);
		List<Object> contents = tag.contents;

		indent(out, level, inline);

		write(out, LT);
		write(out, name);

		if (tag.ctx != null) {
			write(out, _H);
			attrToStr(out, tag, "_h", tag._hnd);
			write(out, DQUOTES);
		}

		for (Entry<String, String> e : tag.attrs.entrySet()) {
			String attr = e.getKey();

			write(out, Constants.SPACE_);
			write(out, HTML.escape(attr));
			write(out, EQ_DQUOTES);
			attrToStr(out, tag, attr, e.getValue());
			write(out, DQUOTES);
		}

		for (String attr : tag.battrs) {
			write(out, Constants.SPACE_);
			write(out, HTML.escape(attr));
		}

		for (String event : tag.eventHandlers.keySet()) {
			write(out, ON);
			write(out, event);
			write(out, EQ_DQUOTES);
			write(out, EMIT);
			write(out, event);
			write(out, EMIT_CLOSE);
			write(out, DQUOTES);
		}

		write(out, GT);

		if (isSingleTag(name)) {
			return;
		}

		if (contents == null || contents.isEmpty()) {
			closeTag(out, name);
			return;
		}

		if (inline || shouldRenderInline(name, contents)) {
			str(contents, level + 1, true, extra, out);
			closeTag(out, name);
			return;
		}

		if (contents != null) {
			str(contents, level, inline, extra, out);
		}

		write(out, Constants.CR_LF);
		indent(out, level, inline);
		closeTag(out, name);
	}

	private void closeTag(OutputStream out, String name) {
		write(out, LT_SLASH);
		write(out, name);
		write(out, GT);
	}

	protected boolean isSingleTag(String name) {
		return name.equals("input");
	}

	protected void attrToStr(OutputStream out, TagImpl<?> tag, String attr, Object value) {
		if (value == null) {
			throw U.rte("The HTML attribute '%s' of tag '%s' cannot have null value!", attr, tag.name);
		}

		if (value instanceof Object[]) {
			Object[] arr = (Object[]) value;
			write(out, U.join(" ", arr));
			return;
		}

		if (value instanceof Collection) {
			Collection<?> coll = (Collection<?>) value;
			write(out, U.join(" ", coll));
			return;
		}

		write(out, HTML.escape(value.toString()));
	}

	protected boolean shouldRenderInline(String name, Object content) {
		if (isSimpleContent(content)) {
			return true;
		}

		if (content instanceof Object[]) {
			return hasSimpleContent((Object[]) content);
		}

		if (content instanceof Collection) {
			return hasSimpleContent((Collection<?>) content);
		}

		return false;
	}

	protected boolean isSimpleContent(Object content) {
		if (content instanceof Var) {
			Var<?> var = (Var<?>) content;
			return isSimpleContent(var.get());
		}

		return !U.instanceOf(content, Tag.class, CustomTag.class, TagWidget.class, Object[].class, Collection.class);
	}

	protected boolean hasSimpleContent(Collection<?> content) {
		for (Object cnt : content) {
			if (!isSimpleContent(cnt)) {
				return false;
			}

		}
		return true;
	}

	protected boolean hasSimpleContent(Object[] content) {
		for (Object cnt : content) {
			if (isSimpleContent(cnt)) {
				return true;
			}
		}
		return false;
	}

	protected void write(OutputStream out, byte[] bytes) {
		try {
			out.write(bytes);
		} catch (IOException e) {
			throw U.rte("Cannot render tag!", e);
		}
	}

	protected void write(OutputStream out, String s) {
		write(out, s.getBytes());
	}

	protected void indent(OutputStream out, int level, boolean inline) {
		if (!inline) {
			for (int i = 0; i < level; i++) {
				write(out, INDENT);
			}
		}
	}

}
