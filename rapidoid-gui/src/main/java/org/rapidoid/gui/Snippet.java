package org.rapidoid.gui;

/*
 * #%L
 * rapidoid-gui
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
import org.rapidoid.commons.Str;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.html.Tag;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.3.0")
public class Snippet extends AbstractWidget<Snippet> {

	private static final String JAVA_KEYWORDS = "abstract|continue|for|new|switch|assert|default|goto|package|synchronized|do|if|private|this|break|implements|protected|throw|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|try|final|interface|static|class|finally|strictfp|volatile|const|native|super|while";

	private static final String PRIMITIVE_TYPES = "void|int|short|char|double|float|long|byte|boolean";

	private static final String tab = "\\t";
	private static final String str1 = "(\"[^\"]*?\")";
	private static final String str2 = "('[^']*?')";
	private static final String num = "(\\d+)";
	private static final String kw = "\\b(" + JAVA_KEYWORDS + ")\\b";
	private static final String anno = "(\\@\\w+?)\\b";
	private static final String types = U.frmt("\\b([A-Z]\\w+?|%s)\\b", PRIMITIVE_TYPES);

	private static final String regex = "(?:" + U.join("|", str1, str2, num, tab, kw, anno, types) + ")";

	protected String code;

	public Snippet(String code) {
		this.code = code;
	}

	@Override
	protected Tag render() {
		return GUI.hardcoded("<pre class=\"code-snippet\">" + prettify() + "</pre>");
	}

	public static String prettify(String sourceCode, boolean escape) {
		// ignoring "\"" => "&quot
		String snippet = escape ? sourceCode.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
			: sourceCode;

		final String prim = "|" + PRIMITIVE_TYPES + "|";

		snippet = Str.replace(snippet, regex, new Mapper<String[], String>() {
			@Override
			public String map(String[] src) throws Exception {
				String s = src[0];
				char ch = s.charAt(0);

				if (Character.isUpperCase(ch) || prim.contains("|" + s + "|")) {
					return "<span class=\"_code_cls\">" + s + "</span>";
				} else if (ch == '"' || ch == "'".charAt(0)) {
					return "<span class=\"_code_str\">" + s + "</span>";
				} else if (ch == '@') {
					return "<span class=\"_code_ann\">" + s + "</span>";
				} else if (Character.isDigit(ch)) {
					return "<span class=\"_code_num\">" + s + "</span>";
				} else if (s.equals("\t")) {
					return "    ";
				} else {
					return "<span class=\"_code_kw\">" + s + "</span>";
				}
			}
		});

		snippet = snippet.replaceAll("\n(\\s*)(.*)\\s//\\shere", "\n$1<span class=\"important-code\">$2</span>");

		return snippet.trim();
	}

	public String prettify() {
		return prettify(code, true);
	}

}
