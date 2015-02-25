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
import org.rapidoid.config.Conf;
import org.rapidoid.db.DB;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.HttpBuiltins;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.oauth.OAuth;
import org.rapidoid.util.AppCtx;
import org.rapidoid.util.Classes;
import org.rapidoid.util.Cls;
import org.rapidoid.util.IO;
import org.rapidoid.util.Scan;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Examples {

	private static final int UPPER = 930;

	private static final int LOWER = 30;

	private static final String JAVA_KEYWORDS = "abstract|continue|for|new|switch|assert|default|goto|package|synchronized|boolean|do|if|private|this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|const|float|native|super|while";

	private static final String tab = "\\t";
	private static final String str1 = "(\"[^\"]*?\")";
	private static final String str2 = "('[^']*?')";
	private static final String kw = "\\b(" + JAVA_KEYWORDS + ")\\b";
	private static final String cls = "\\b([A-Z]\\w+?)\\b";
	private static final String rr = "(?:" + U.join("|", str1, str2, tab, kw, cls) + ")";
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

		for (int i = 1; i <= LOWER; i++) {
			processExample(path, server, egNum(i));
		}

		for (int i = 900; i <= UPPER; i++) {
			processExample(path, server, egNum(i));
		}

		generateIndex(path);

		server.shutdown();

		DB.shutdown();
		DB.destroy();
	}

	private static void processExample(String path, HTTPServer server, String id) {
		String here = "./src/main/java/";
		String pkg = "org.rapidoid.docs.eg" + id;

		String egDir = here + pkg.replace('.', '/');
		if (!new File(egDir).exists()) {
			return;
		}

		AppCtx.reset();
		List<Class<?>> classes = Scan.pkg(pkg);
		U.must(!classes.isEmpty());

		generate(server, path, id, classes);
	}

	private static void generateIndex(String path) {
		String docsT = IO.load("docs-template.html");
		String egT = IO.load("example-template.html");

		String examples = "";

		for (int i = 2; i <= LOWER; i++) {
			examples = processIndex(egT, examples, egNum(i));
		}

		for (int i = 900; i <= UPPER; i++) {
			examples = processIndex(egT, examples, egNum(i));
		}

		String html = UTILS.fillIn(docsT, "examples", examples);

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

		snippet = col(rr, snippet);

		snippet = snippet.replaceAll("\n(\\s*)(.*)\\s//\\shere", "\n$1<span class=\"important-code\">$2</span>");

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

	private static String col(String rr, String snippet) {
		snippet = UTILS.replace(snippet, rr, new Mapper<String[], String>() {
			@Override
			public String map(String[] src) throws Exception {
				String s = src[0];
				char ch = s.charAt(0);
				if (Character.isUpperCase(ch)) {
					return "<span class=\"_code_cls\">" + s + "</span>";
				} else if (ch == '"' || ch == "'".charAt(0)) {
					return "<span class=\"_code_str\">" + s + "</span>";
				} else if (s.equals("\t")) {
					return "    ";
				} else {
					return "<span class=\"_code_kw\">" + s + "</span>";
				}
			}
		});
		return snippet;
	}

	public static void generate(HTTPServer server, String path, String id, List<Class<?>> classes) {

		Classes appClasses = Classes.from(classes);
		AppCtx.setClasses(appClasses);
		DB.destroy();

		DB.start();

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

		path += "examples/";
		new File(path).mkdir();

		path += id + "/";
		new File(path).mkdir();

		saveTo(server, "/", path + "index.html");
		saveTo(server, "/search", path + "search.html");
		saveTo(server, "/rapidoid.js", path + "../rapidoid.js");
		saveTo(server, "/rapidoid.css", path + "../rapidoid.css");

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

		AppCtx.delClasses();
	}

	private static void saveTo(HTTPServer server, String url, String filename) {
		String out = server.process(U.format("GET %s HTTP/1.1\r\nHost: a.b\r\n\r\n", url));

		int p = out.indexOf("\r\n\r\n");
		out = out.substring(p + 4);

		// TODO remove these hacks
		out = out.replace("/rapidoid.css", "../rapidoid.css");
		out = out.replace("/rapidoid.js", "../rapidoid.js");
		out = out.replace("/bootstrap/css/theme-", "../theme-");
		out = out.replace("\"//", "\"http://");
		out = out.replace("href=\"/\"", "href=\"index.html\"");
		out = out.replaceAll("(href|action)=\\\"/(\\w+)\\\"", "$1=\"$2.html\"");

		IO.save(filename, out);
	}
}
