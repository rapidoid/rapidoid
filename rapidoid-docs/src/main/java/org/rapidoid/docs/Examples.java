package org.rapidoid.docs;

/*
 * #%L
 * rapidoid-docs
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
import java.lang.reflect.Method;
import java.net.BindException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.AppHandler;
import org.rapidoid.app.Apps;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.ctx.Classes;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.HttpBuiltins;
import org.rapidoid.http.HttpExchangeImpl;
import org.rapidoid.oauth.OAuth;
import org.rapidoid.quick.Quick;
import org.rapidoid.scan.Scan;
import org.rapidoid.util.IO;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.widget.BootstrapWidgets;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Examples {

	private static final int UPPER = 930;
	private static final int LOWER = 30;

	private static final Pattern p = Pattern.compile("\n");

	public static void main(String[] args) {
		Conf.args("oauth-no-state", "generate", "oauth-domain=https://rapidoid.io");

		String path = "../../rapidoid.github.io/";
		U.must(new File(path).exists());

		HTTPServer server = HTTP.server().build();
		OAuth.register(server);
		HttpBuiltins.register(server);
		server.serve(new AppHandler());
		server.start();

		// FIXME For each example a server is created internally, trying to bind
		// to 8080, which is already taken by the server above. Error messages
		// appear, but the examples are successfully generated.

		for (int i = 1; i <= LOWER; i++) {
			processExample(path, server, egNum(i));
		}

		for (int i = 900; i <= UPPER; i++) {
			processExample(path, server, egNum(i));
		}

		generateIndex(path);

		server.shutdown();
	}

	private static void processExample(String path, HTTPServer server, String id) {
		String here = "./src/main/java/";
		String pkg = "org.rapidoid.docs.eg" + id;

		String egDir = here + pkg.replace('.', '/');
		if (!new File(egDir).exists()) {
			return;
		}

		Ctx.reset();
		Scan.reset();
		Apps.reset();

		List<Class<?>> classes = Scan.pkg(pkg);
		U.must(!classes.isEmpty());

		generate(server, path, id, classes);
	}

	private static void generateIndex(String path) {
		String expressions = IO.load("expressions.html");
		String docsT = IO.load("docs-template.html");
		String egT = IO.load("example-template.html");

		String examples = "";

		Ctx.setExchange(new HttpExchangeImpl());

		for (int i = 2; i <= LOWER; i++) {
			examples = processIndex(egT, examples, egNum(i));
		}

		for (int i = 900; i <= UPPER; i++) {
			examples = processIndex(egT, examples, egNum(i));
		}

		String html = UTILS.fillIn(docsT, "expressions", expressions);
		html = UTILS.fillIn(html, "examples", examples);

		IO.save(path + "index.html", html);
	}

	private static String processIndex(String egT, String examples, String id) {
		String snippFile = "src/main/java/org/rapidoid/docs/eg" + id + "/App.java";
		String snippet = IO.load(snippFile);

		if (snippet == null) {
			return examples;
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

		snippet = BootstrapWidgets.snippet(snippet).prettify();

		String titleInfo = "";
		String fullTitle = title + titleInfo;

		String example = UTILS.fillIn(egT, "n", id);
		example = UTILS.fillIn(example, "code", snippet);
		example = UTILS.fillIn(example, "title", fullTitle);
		example = UTILS.fillIn(example, "desc", desc);
		examples += example;
		return examples;
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

	public static void generate(HTTPServer server, String path, String id, List<Class<?>> classes) {

		Classes appClasses = Classes.from(classes);
		Ctx.setClasses(appClasses);

		Class<?> appCls = appClasses.get("App");
		if (appCls != null) {
			Method main = Cls.getMethod(appCls, "main", String[].class);
			try {
				Cls.invokeStatic(main, (Object) new String[0]);
			} catch (Throwable e) {
				if (!(UTILS.rootCause(e) instanceof BindException)) {
					e.printStackTrace();
				}
			}
		}

		Conf.args("oauth-no-state", "generate", "oauth-domain=https://rapidoid.io");

		Quick.bootstrap();

		path += "examples/";
		new File(path).mkdir();

		path += id + "/";
		new File(path).mkdir();

		saveTo(server, "/", path + "index.html");
		saveTo(server, "/search", path + "search.html");
		saveTo(server, "/rapidoid.js", path + "../rapidoid.js");
		saveTo(server, "/rapidoid.css", path + "../rapidoid.css");
		saveTo(server, "/ng-infinite-scroll-1.0.min.js", path + "../ng-infinite-scroll-1.0.min.js");

		for (int i = 1; i <= 5; i++) {
			saveTo(server, "//bootstrap/css/theme-" + i + ".css", path + "../theme-" + i + ".css");
		}

		if (appClasses.containsKey("Movie")) {
			for (int i = 1; i <= 5; i++) {
				saveTo(server, "/movie/" + i, path + "movie" + i + ".html");
			}
		}

		for (Class<?> cls : classes) {
			String name = cls.getSimpleName();
			if (name.endsWith("Screen") && !name.equals("HomeScreen")) {
				name = U.mid(name, 0, -6);
				String page = U.uncapitalized(name);
				String url = "/" + page;
				saveTo(server, url, path + page + ".html");
			}
		}

		Ctx.delClasses();
	}

	private static void saveTo(HTTPServer server, String url, String filename) {
		String out = server.process(U.format("GET %s HTTP/1.1\r\nHost: a.b\r\n\r\n", url));

		int p = out.indexOf("\r\n\r\n");
		out = out.substring(p + 4);

		// TODO remove these hacks
		out = out.replace("/rapidoid.css", "../rapidoid.css");
		out = out.replace("/rapidoid.js", "../rapidoid.js");
		out = out.replace("/ng-infinite-scroll-1.0.min.js", "../ng-infinite-scroll-1.0.min.js");
		out = out.replace("/bootstrap/css/theme-", "../theme-");
		out = out.replace("\"//", "\"http://");
		out = out.replace("href=\"/\"", "href=\"index.html\"");
		out = out.replaceAll("(href|action)=\\\"/(\\w+)\\\"", "$1=\"$2.html\"");
		out = out.replaceAll("_emit\\('\\d+'\\)", "alert('This is not a live demo, so this button does NOT work!');");

		IO.save(filename, out);
	}
}
