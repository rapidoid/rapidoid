package org.rapidoid.pages;

/*
 * #%L
 * rapidoid-pages
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.html.Tag;
import org.rapidoid.pages.impl.FileTemplateTag;
import org.rapidoid.pages.impl.MultiLanguageText;
import org.rapidoid.plugins.templates.ITemplate;
import org.rapidoid.plugins.templates.Templates;
import org.rapidoid.util.U;
import org.rapidoid.widget.BootstrapWidgets;
import org.rapidoid.widget.ButtonWidget;

@Authors("Nikolche Mihajlovski")
@Since("2.3.0")
public class PageGUI extends BootstrapWidgets {

	public static Object i18n(String multiLanguageText, Object... formatArgs) {
		return new MultiLanguageText(multiLanguageText, formatArgs);
	}

	public static Tag render(String templateFileName, Object... namesAndValues) {
		return render(Templates.fromFile(templateFileName), namesAndValues);
	}

	public static Tag render(ITemplate template, Object... namesAndValues) {
		return new FileTemplateTag(template, namesAndValues);
	}

	public static Tag modal(Object title, Object content, Object footer) {
		throw U.notSupported();
	}

	public static ButtonWidget xClose(String cmd) {
		Tag sp1 = span(hardcoded("&times;")).attr("aria-hidden", "true");
		Tag sp2 = span("Close").class_("sr-only");
		return cmd(cmd).class_("close").contents(sp1, sp2);
	}

	public static Tag page(String pageTitle, Object head, Object body) {
		return render("page.html", "title", pageTitle, "head_extra", head, "content", body);
	}

	public static Tag page(String pageTitle, Object body) {
		return page(pageTitle, "", body);
	}

}
