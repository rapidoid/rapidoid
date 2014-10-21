package com.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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
import java.io.PrintStream;

import org.rapidoid.util.U;

public class HTMLSnippets {

	private static String PAGE_HTML;

	static {
		PAGE_HTML = U.load("page.html");
	}

	public static void errorPage(HttpExchange x, String title, Throwable err) {

		String content = stackTrace("Stack trace: ", err);

		String html = PAGE_HTML.replaceAll("\\{\\{title\\}\\}", title).replaceAll("\\{\\{content\\}\\}", content);

		x.write(html);
	}

	private static String stackTrace(String title, Throwable err) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		err.printStackTrace(new PrintStream(buf));

		String trace = buf.toString().replace('$', '_').replaceAll("\r?\n", "<br/>");

		String content = "<h4>" + title + "</h4>" + trace;

		if (err.getCause() != null) {
			content += stackTrace("Cause:", err.getCause());
		}

		return content;
	}

}
