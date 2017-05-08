package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Arr;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-render
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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Templates extends RapidoidThing {

	private static final String DEFAULT_TEMPLATES = "default/templates";

	public static final String[] DEFAULT_PATH = {"templates", "", DEFAULT_TEMPLATES};

	public static final TemplateStore DEFAULT_STORE = new FileSystemTemplateStore(DEFAULT_PATH);

	public static final RapidoidTemplateFactory DEFAULT_FACTORY = new RapidoidTemplateFactory("templates", DEFAULT_STORE);

	private static volatile String[] PATH = DEFAULT_PATH;

	public static Template load(String filename, Class<?> modelType) {
		return DEFAULT_FACTORY.load(filename, modelType);
	}

	public static Template load(String filename) {
		return load(filename, Object.class);
	}

	public static Template compile(String source, Class<?> modelType) {
		return DEFAULT_FACTORY.compile(source, modelType);
	}

	public static Template compile(String source) {
		return compile(source, Object.class);
	}

	public static void reset() {
		PATH = DEFAULT_PATH;
		DEFAULT_FACTORY.reset();
	}

	public static void setPath(String... templatesPath) {
		PATH = templatesPath;
	}

	public static String[] getPath() {
		return PATH;
	}

	public static String[] withDefaultPath(String[] templatesPath) {
		if (U.isEmpty(templatesPath) || U.neq(U.last(templatesPath), Templates.DEFAULT_TEMPLATES)) {
			return Arr.concat(templatesPath, Templates.DEFAULT_TEMPLATES);
		} else {
			return templatesPath;
		}
	}

}
