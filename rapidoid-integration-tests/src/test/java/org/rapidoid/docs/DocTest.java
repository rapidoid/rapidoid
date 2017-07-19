package org.rapidoid.docs;

/*
 * #%L
 * rapidoid-integration-tests
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

import org.junit.Test;
import org.rapidoid.commons.Str;
import org.rapidoid.fluent.Do;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.io.FileSearchResult;
import org.rapidoid.io.IO;
import org.rapidoid.log.GlobalCfg;
import org.rapidoid.test.Doc;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class used as entry point, to execute an example and generate the docs.
 * <p>
 * This test will execute the main class specified in the annotation.
 */
public abstract class DocTest extends IsolatedIntegrationTest {

	private static final String LICENSE_HEADER = "(?sm)\\Q\n/*\n * #" + "%L\\E(.*?)\\Q * #L%\n */\n\\E";

	private final AtomicInteger order = new AtomicInteger();

	@Test
	public void docs() throws Exception {
		order.set(0);

		exercise();

		generateDocs();
	}

	private void generateDocs() {

		Doc doc = getTestAnnotation(Doc.class);
		U.notNull(doc, "@Doc");

		if (!doc.show()) return;

		Map<String, String> files = scanFiles();

		String pkg = getTestPackageName();
		pkg = Str.triml(pkg, "org.rapidoid.docs.");

		String dir = System.getProperty("user.dir");
		dir = Str.cutToFirst(dir, "rapidoid") + "rapidoid";

		String asciidoc = Msc.path(dir, "asciidoc", "examples");
		new File(asciidoc).mkdirs();

		generateAsciiDoc(doc, pkg, asciidoc, files);
	}

	private void generateAsciiDoc(Doc doc, String id, String asciidoc, Map<String, String> files) {
		StringBuilder sb = new StringBuilder();

		sb.append("=== " + doc.title());

		files.forEach((name, content) -> {
			String ext = Str.cutFromLast(name, ".");

			sb.append("[[app-listing]]\n");
			sb.append(U.frmt("[source,%s]\n", ext));
			sb.append("." + name + "\n");
			sb.append("----\n");
			sb.append(cleanCode(content, ext));
			sb.append("\n----\n\n");
		});

		String filename = id + ".adoc";

		IO.save(Msc.path(asciidoc, filename), sb.toString());

		if (GlobalCfg.is("DOCS")) {
			appendToIndex(asciidoc, filename);
		}
	}

	private void appendToIndex(String asciidoc, String filename) {
		String toIndex = U.frmt("include::%s[]\n\n", filename);

		String index = Msc.path(asciidoc, "index.adoc");

		IO.append(index, toIndex.getBytes());
	}

	private String cleanCode(String code, String ext) {
		if (ext.equals("java")) {
			code = code.replaceAll(LICENSE_HEADER, "");
		}

		return code.trim();
	}

	private Map<String, String> scanFiles() {
		List<FileSearchResult> filenames = U.list();

		String dir = System.getProperty("user.dir");

		if (!dir.endsWith("rapidoid-integration-tests")) {
			dir = Msc.path(dir, "rapidoid-integration-tests");
		}

		String pkg = getTestPackageName().replace('.', File.separatorChar);

		String javaDir = Msc.path(dir, "src", "test", "java", pkg);
		String resDir = Msc.path(dir, "src", "test", "resources", pkg);

		List<FileSearchResult> java = IO.find().files().in(javaDir).recursive().ignoreRegex(".*Test\\.java").getResults();
		Collections.sort(java);
		filenames.addAll(java);

		List<FileSearchResult> res = IO.find().files().in(resDir).recursive().getResults();
		Collections.sort(res);
		filenames.addAll(res);

		return Do.map(filenames).to(f -> f.relativeName(), f -> IO.load(f.absoluteName()));
	}

	protected void exercise() {
		// by default do nothing
	}

	private String order() {
		return "#" + order.incrementAndGet();
	}

	protected void GET(String uri) {
		getReq(uri + order());
	}

	protected void POST(String uri) {
		postJson(uri + order(), U.map());
	}

	protected void POST(String uri, Map<String, ?> data) {
		postJson(uri + order(), data);
	}

	protected void PUT(String uri) {
		putData(uri + order(), U.map());
	}

	protected void PUT(String uri, Map<String, ?> data) {
		putData(uri + order(), data);
	}

	protected void DELETE(String uri) {
		deleteReq(uri + order());
	}

}
