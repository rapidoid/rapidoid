package org.rapidoid.pages.util;

/*
 * #%L
 * rapidoid-html
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

import java.io.File;
import java.util.List;

import org.rapidoid.util.U;

/**
 * This program generates the Java source code for all the tags in the framework.
 */
public class Generator {

	public static void main(String[] args) {

		File file = new File(U.resource("html-tags.txt").getFile());
		File rapidoidPages = file.getParentFile().getParentFile().getParentFile();
		File path = new File(rapidoidPages, "src/main/java/org/rapidoid/pages/html");

		U.must(path.exists());

		String tagT = U.load("tag-method-template.txt");
		String foundationT = U.load("tags-template.java");
		String tagInterfaceT = U.load("tag-template.java");
		List<String> tags = U.loadLines("html-tags.txt");
		String s = "";

		for (String tag : tags) {
			String tagCap = U.capitalized(tag);
			String name = tagCap + "Tag";

			String piece = U.fillIn(tagT, "tag", tag);
			piece = U.fillIn(piece, "type", name);
			s += piece;

			U.save(path.getAbsolutePath() + "/" + name + ".java", tagInterface(tagInterfaceT, name));
		}

		String fnd = U.fillIn(foundationT, "tags", s);

		U.save(path.getAbsolutePath() + "/Tags.java", fnd);
	}

	private static String tagInterface(String tagInterfaceT, String name) {
		String code = U.fillIn(tagInterfaceT, "name", name);

		String content = U.render(tagMethods(name), "    %s;", "\n\n");
		code = U.fillIn(code, "content", content);

		return code;

	}

	private static List<String> tagMethods(String tag) {

		List<String> methods = U.list();

		if (tag.equals("InputTag")) {
			methods.add("String value()");
			methods.add("InputTag value(String value)");
		} else if (tag.equals("ATag")) {
			methods.add("String href()");
			methods.add("ATag href(String href)");
		}

		return methods;
	}

}
