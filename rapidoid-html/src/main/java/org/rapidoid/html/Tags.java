package org.rapidoid.html;

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

import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;
import org.rapidoid.html.impl.ConstantTag;
import org.rapidoid.html.impl.TagContextImpl;
import org.rapidoid.html.impl.TagProxy;
import org.rapidoid.html.impl.UndefinedTag;
import org.rapidoid.html.tag.InputTag;
import org.rapidoid.html.tag.OptionTag;
import org.rapidoid.html.tag.TextareaTag;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.reactive.Var;
import org.rapidoid.reactive.Vars;
import org.rapidoid.util.U;

public class Tags extends BasicUtils {

	public static final Object $index = new Object();
	public static final Object $key = new Object();
	public static final Object $value = new Object();

	public static <T> Var<T> var(T value) {
		return Vars.var(value);
	}

	public static TagContext context() {
		return new TagContextImpl();
	}

	public static <TAG extends Tag<?>> TAG tag(Class<TAG> clazz, String tagName, Object... contents) {
		return TagProxy.create(clazz, tagName, contents);
	}

	public static ConstantTag constant(String code) {
		return new ConstantTag(code);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] foreach(Object[] values, Tag<?> template) {
		Tag<?>[] tags = new Tag[values.length];

		for (int i = 0; i < values.length; i++) {
			tags[i] = template.copy();
			replaceAll(tags[i], $index, i, $value, values[i]);
		}

		return (T[]) tags;
	}

	public static void replaceAll(Tag<?> tag, final Object... findAndReplace) {
		U.notNull(tag, "tag");

		transform(tag, new Mapper<Object, Object>() {
			@Override
			public Object map(Object obj) throws Exception {

				for (int i = 0; i < findAndReplace.length / 2; i++) {
					Object find = findAndReplace[i * 2];
					Object replace = findAndReplace[i * 2 + 1];

					if (find.equals(obj)) {
						return replace;
					}
				}

				return obj;
			}
		});
	}

	public static void transform(Tag<?> tag, Mapper<Object, Object> transformation) {
		U.notNull(tag, "tag");

		if (tag instanceof UndefinedTag) {
			return;
		}

		for (int i = 0; i < tag.size(); i++) {
			Object child = tag.child(i);

			Object transformed = U.eval(transformation, child);

			if (transformed != child) {
				tag.setChild(i, transformed);
			} else {
				if (transformed instanceof Tag<?>) {
					transform((Tag<?>) transformed, transformation);
				} else if (transformed instanceof TagWidget) {
					transform(((TagWidget<?>) transformed).view(null), transformation);
				}
			}
		}
	}

	public static String escape(String s) {
		return StringEscapeUtils.escapeHtml4(s);
	}

	public static void traverse(Object contents, TagProcessor<Tag<?>> processor) {

		if (contents instanceof Tag) {
			if (contents instanceof UndefinedTag) {
				UndefinedTag<?> tag = (UndefinedTag<?>) contents;
				tag.traverse(processor);
			} else {
				Tag<?> tag = (Tag<?>) contents;
				processor.handle(tag);
				traverse(tag.content(), processor);
			}
		} else if (contents instanceof TagWidget) {
			traverse(((TagWidget<?>) contents).view(null), processor);
		} else if (contents instanceof Object[]) {
			Object[] arr = (Object[]) contents;
			for (Object cont : arr) {
				traverse(cont, processor);
			}
		} else if (contents instanceof Collection<?>) {
			Collection<?> coll = (Collection<?>) contents;
			for (Object cont : coll) {
				traverse(cont, processor);
			}
		}
	}

	public static <T extends Tag<?>> T setValue(T tag, Object value) {

		if (tag instanceof InputTag) {
			InputTag input = (InputTag) tag;
			if ("checkbox".equals(input.type()) || "radio".equals(input.type())) {
				input.checked(value != null ? bool(value) : false);
			} else {
				input.value(value != null ? str(value) : "");
			}
		} else if (tag instanceof TextareaTag) {
			TextareaTag textArea = (TextareaTag) tag;
			textArea.content(value != null ? str(value) : "");
		} else if (tag instanceof OptionTag) {
			OptionTag optionTag = (OptionTag) tag;
			optionTag.selected(value != null ? bool(value) : false);
		} else {
			throw U.rte("Cannot set value to a '%s' tag!", ((Tag<?>) tag).tagKind());
		}

		return tag;
	}

	public static Object getValue(Tag<?> tag) {
		throw U.notReady(); // FIXME
	}

}
