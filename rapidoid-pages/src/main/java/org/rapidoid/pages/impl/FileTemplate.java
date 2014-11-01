package org.rapidoid.pages.impl;

/*
 * #%L
 * rapidoid-pages
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

import org.rapidoid.util.U;

public class FileTemplate {

	private final String templateName;

	private final Object[] namesAndValues;

	public FileTemplate(String templateName, Object[] namesAndValues) {
		U.must(U.resource(templateName) != null, "Cannot find file: %s", templateName);

		this.templateName = templateName;
		this.namesAndValues = namesAndValues;
	}

	@Override
	public String toString() {
		String text = U.load(templateName);

		for (int i = 0; i < namesAndValues.length / 2; i++) {
			String placeholder = (String) namesAndValues[i * 2];
			String value = TagRenderer.str(namesAndValues[i * 2 + 1]);

			text = U.fillIn(text, placeholder, value);
		}

		return text;
	}

}
