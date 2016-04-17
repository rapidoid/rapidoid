package org.rapidoid.html;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.html.impl.ConstantTag;
import org.rapidoid.html.impl.TagProxy;
import org.rapidoid.html.impl.UndefinedTag;
import org.rapidoid.html.tag.InputTag;
import org.rapidoid.html.tag.OptionTag;
import org.rapidoid.html.tag.TextareaTag;
import org.rapidoid.u.U;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;

import java.util.Collection;

/*
 * #%L
 * rapidoid-html
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
@Since("2.0.0")
public class Tags extends BasicUtils {

	public static <T> Var<T> var(String name, T value) {
		return Vars.var(name, value);
	}

	public static <TAG extends Tag> TAG tag(Class<TAG> clazz, String tagName, Object... contents) {
		return TagProxy.create(clazz, tagName, contents);
	}

	public static ConstantTag constant(String code) {
		return new ConstantTag(code);
	}

	public static String escape(String s) {
		return Str.xmlEscape(s);
	}

	public static void traverse(Object contents, TagProcessor<Tag> processor) {

		if (contents instanceof Tag) {
			if (contents instanceof UndefinedTag) {
				UndefinedTag tag = (UndefinedTag) contents;
				tag.traverse(processor);
			} else {
				Tag tag = (Tag) contents;
				processor.handle(tag);
				traverse(tag.contents(), processor);
			}
		} else if (contents instanceof TagWidget) {
			Object widgetContent = ((TagWidget<?>) contents).render(null);
			if (widgetContent != null) {
				traverse(widgetContent, processor);
			}
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

	@SuppressWarnings("unchecked")
	public static <T extends Tag> T withValue(T tag, Object value) {

		if (tag instanceof InputTag) {
			InputTag input = (InputTag) tag;
			if ("checkbox".equals(input.type()) || "radio".equals(input.type())) {
				return (T) input.checked(value != null ? bool(value) : false);
			} else {
				return (T) input.value(value != null ? str(value) : "");
			}
		} else if (tag instanceof TextareaTag) {
			TextareaTag textArea = (TextareaTag) tag;
			return (T) textArea.contents(value != null ? str(value) : "");
		} else if (tag instanceof OptionTag) {
			OptionTag optionTag = (OptionTag) tag;
			return (T) optionTag.selected(value != null ? bool(value) : false);
		} else {
			throw U.rte("Cannot set value to a '%s' tag!", ((Tag) tag).tagKind());
		}
	}

}
