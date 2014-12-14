package org.rapidoid.pages;

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

import org.rapidoid.html.HTML;
import org.rapidoid.html.Tag;
import org.rapidoid.model.Item;
import org.rapidoid.pages.impl.FileTemplateTag;
import org.rapidoid.pages.impl.ItemPropertyVar;
import org.rapidoid.pages.impl.MultiLanguageText;
import org.rapidoid.pages.impl.SimpleHardcodedTag;
import org.rapidoid.var.Var;

public abstract class HtmlWidgets extends HTML {

	public static Object i18n(String multiLanguageText, Object... formatArgs) {
		return new MultiLanguageText(multiLanguageText, formatArgs);
	}

	public static <T> Var<T> property(Item item, String property) {
		return new ItemPropertyVar<T>(item, property);
	}

	public static Tag template(String templateFileName, Object... namesAndValues) {
		return new FileTemplateTag(templateFileName, namesAndValues);
	}

	public static Tag hardcoded(String content) {
		return new SimpleHardcodedTag(content);
	}

}
