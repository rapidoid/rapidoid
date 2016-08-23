package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.u.U;

import java.util.Map;

/*
 * #%L
 * rapidoid-render
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Templates extends RapidoidThing {

	static final String DEFAULT_TEMPLATES_PATH = "default/templates";

	public static final String[] DEFAULT_PATH = {"templates", "", DEFAULT_TEMPLATES_PATH};

	public static final TemplateStore DEFAULT_STORE = new FileSystemTemplateStore(DEFAULT_PATH);

	private static volatile String[] PATH = DEFAULT_PATH;

	private static final Map<String, TemplateRenderer> TEMPLATES = Coll.concurrentMap();

	public static TemplateRenderer loadTemplate(String name, TemplateStore store) {
		String content;

		try {
			content = store.loadTemplate(name);
		} catch (Exception e) {
			throw U.rte("Couldn't load template: " + name, e);
		}

		return TemplateParser.parse(content).compile();
	}

	public static Template load(String filename) {
		return load(filename, DEFAULT_STORE);
	}

	public static Template load(String filename, TemplateStore templates) {
		return new RapidoidTemplate(filename, loadTemplate(filename, templates), templates);
	}

	public static Template compile(String source) {
		return new RapidoidTemplate("", TemplateParser.parse(source).compile(), DEFAULT_STORE);
	}

	public static void reset() {
		PATH = DEFAULT_PATH;
		TEMPLATES.clear();
	}

	public static void setPath(String... templatesPath) {
		PATH = templatesPath;
	}

	public static String[] getPath() {
		return PATH;
	}

}
