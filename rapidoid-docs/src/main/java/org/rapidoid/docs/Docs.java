package org.rapidoid.docs;

/*
 * #%L
 * rapidoid-docs
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

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.AppHandler;
import org.rapidoid.config.Config;
import org.rapidoid.ctx.Classes;
import org.rapidoid.http.HTTP;
import org.rapidoid.io.IO;
import org.rapidoid.main.Rapidoid;
import org.rapidoid.plugins.templates.Templates;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.util.U;
import org.rapidoid.webapp.AppMode;
import org.rapidoid.webapp.WebApp;
import org.rapidoid.webapp.WebAppGroup;
import org.rapidoid.widget.SnippetWidget;
import org.rapidoid.wrap.IntWrap;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Docs {

	private static final Pattern p = Pattern.compile("\n");

	public static void main(String[] args) {
		// Log.setLogLevel(LogLevel.DEBUG);
		ClasspathUtil.setIgnoreRapidoidClasses(false);
		Rapidoid.run(args);

		String path = "../../rapidoid.github.io/";
		U.must(new File(path).exists());

		generateIndex(path);

		System.exit(0);
	}

	private static void generateIndex(String path) {
		System.out.println();
		System.out.println();
		System.out.println("*************** " + path);
		System.out.println();
		System.out.println();

		List<Map<String, ?>> examples = U.list();

		IntWrap nn = new IntWrap();

		// processFirst(examples, nn, 3);
		processAll(examples, nn);

		Map<String, ?> model = U.map("examples", examples);
		String html = Templates.fromFile("docs.html").render(model);
		IO.save(path + "index.html", html);
	}

	@SuppressWarnings("unused")
	private static void processFirst(List<Map<String, ?>> examples, IntWrap nn, int firstN) {
		for (int i = 1; i <= firstN; i++) {
			processIndex(nn, examples, egNum(i));
		}
	}

	private static void processAll(List<Map<String, ?>> examples, IntWrap nn) {
		for (int i = 1; i <= 30; i++) {
			processIndex(nn, examples, egNum(i));
		}

		for (int i = 100; i <= 130; i++) {
			processIndex(nn, examples, egNum(i));
		}

		for (int i = 900; i <= 930; i++) {
			processIndex(nn, examples, egNum(i));
		}
	}

	private static void processIndex(IntWrap nn, List<Map<String, ?>> examples, String id) {
		System.out.println();
		System.out.println();
		System.out.println(id);
		System.out.println();
		System.out.println();
		String snippFile = "src/main/java/org/rapidoid/docs/eg" + id + "/Main.java";

		String snippet = IO.load(snippFile);

		if (snippet == null) {
			return;
		}

		snippet = cleanSnippet(snippet);

		Matcher m = p.matcher(snippet);
		U.must(m.find());
		int pos = m.start();

		String titleAndDesc = snippet.substring(0, pos).trim();
		U.must(titleAndDesc.startsWith("//"));
		titleAndDesc = titleAndDesc.substring(2).trim();

		String[] titleAndDescParts = titleAndDesc.split("\\s*::\\s*");
		U.must(titleAndDescParts.length >= 2);
		String title = titleAndDescParts[0];
		String desc = titleAndDescParts[1];

		snippet = snippet.substring(pos).trim();
		snippet = SnippetWidget.prettify(snippet, true);

		String titleInfo = "";
		String fullTitle = title + titleInfo;

		Iterable<Class<?>> clss = ClasspathUtil.scanClasses("org.rapidoid.docs.eg" + id, null, null, null, null);

		Classes classes = Classes.from(clss);
		Config config = new Config();
		WebApp app = new WebApp("eg" + id, null, null, U.set("/"), AppMode.PRODUCTION, null, null, classes, config);
		app.getRouter().generic(new AppHandler());

		WebAppGroup.main().clear();
		WebAppGroup.main().setDefaultApp(app);

		List<?> results = getResults(id);

		nn.value++;
		Map<String, ?> model = U.map("n", id, "nn", nn.value, "code", snippet, "title", fullTitle, "desc", desc,
				"results", results);
		examples.add(model);

		WebAppGroup.main().setDefaultApp(null);
	}

	private static List<?> getResults(String id) {
		List<String> lines = IO.loadLines("eg/" + id + ".txt");

		List<Object> results = U.list();

		if (lines == null) {
			lines = U.list("GET /");
		}

		if (lines != null) {
			for (String line : lines) {
				Map<String, ?> res = getResult(line);
				results.add(res);
			}
		}

		return results;
	}

	private static Map<String, String> getResult(String line) {
		String[] parts = line.split("\\s", 2);
		String verb = parts[0];
		String uri = parts[1];

		String uri2 = uri.contains("?") ? uri.replace("?", "?embedded=true&") : uri + "?embedded=true";

		String result = null;

		try {
			if ("GET".equalsIgnoreCase(verb)) {
				result = new String(HTTP.get("http://localhost:8080" + uri2));
			} else if ("POST".equalsIgnoreCase(verb)) {
				result = new String(HTTP.post("http://localhost:8080" + uri2, null, null, null));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "<b>404 Not found!</b>";
		}

		if (result == null) {
			result = nonHttpResult(line);
		}

		return U.map("verb", verb, "uri", uri, "result", result);
	}

	private static String nonHttpResult(String line) {
		return "?";
	}

	private static String egNum(int n) {
		return n < 10 ? "00" + n : n < 100 ? "0" + n : "" + n;
	}

	private static String cleanSnippet(String s) {
		String comm = "#L%\n */";
		int p = s.indexOf(comm);
		U.must(p > 0);
		s = s.substring(p + comm.length()).trim();
		return s;
	}

}
