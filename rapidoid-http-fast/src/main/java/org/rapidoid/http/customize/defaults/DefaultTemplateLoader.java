package org.rapidoid.http.customize.defaults;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.customize.ResourceLoader;
import org.rapidoid.io.Res;
import org.rapidoid.render.Templates;

/*
 * #%L
 * rapidoid-http-fast
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
@Since("5.2.0")
public class DefaultTemplateLoader extends RapidoidThing implements ResourceLoader {

	private final String[] templatesPath;

	public DefaultTemplateLoader(String[] templatesPath) {
		this.templatesPath = templatesPath;
	}

	@Override
	public byte[] load(String resourceName) throws Exception {
		String[] path = Templates.withDefaultPath(templatesPath);
		return Res.from(resourceName, path).getBytesOrNull();
	}

	public String[] templatesPath() {
		return templatesPath;
	}
}
