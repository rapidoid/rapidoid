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
import org.rapidoid.ctx.Ctx;
import org.rapidoid.html.Tag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.io.IO;
import org.rapidoid.pages.impl.FileTemplateTag;
import org.rapidoid.pages.impl.MultiLanguageText;
import org.rapidoid.pages.impl.StateTag;
import org.rapidoid.widget.BootstrapWidgets;
import org.rapidoid.widget.ButtonWidget;

@Authors("Nikolche Mihajlovski")
@Since("2.3.0")
public class PageGUI extends BootstrapWidgets {

	public static Object i18n(String multiLanguageText, Object... formatArgs) {
		return new MultiLanguageText(multiLanguageText, formatArgs);
	}

	public static Tag render(String templateFileName, Object... namesAndValues) {
		return new FileTemplateTag(templateFileName, namesAndValues);
	}

	public static Tag modal(Object title, Object content, Object footer) {
		return render("modal.html", "title", title, "content", content, "footer", footer, "cmdCloseModal",
				xClose("closeModal"));
	}

	public static ButtonWidget xClose(String cmd) {
		Tag sp1 = span(hardcoded("&times;")).attr("aria-hidden", "true");
		Tag sp2 = span("Close").class_("sr-only");
		return cmd(cmd).class_("close").contents(sp1, sp2);
	}

	public static Tag page(boolean devMode, String pageTitle, Object head, Object body) {
		String devOrProd = devMode ? "dev" : "prod";

		Tag assets = hardcoded(IO.loadResourceAsString("page-assets-" + devOrProd + ".html", true));
		Tag meta = hardcoded(IO.loadResourceAsString("page-meta-" + devOrProd + ".html", true));

		HttpExchange x = Ctx.exchange();
		Object state = new StateTag(x);

		return render("page-" + devOrProd + ".html", "title", pageTitle, "head", head, "body", body, "assets", assets,
				"meta", meta, "state", state);
	}

	public static Tag page(boolean devMode, String pageTitle, Object body) {
		return page(devMode, pageTitle, "", body);
	}

}
