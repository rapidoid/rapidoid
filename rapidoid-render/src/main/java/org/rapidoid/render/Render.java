package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

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
@Since("5.1.0")
public class Render extends RapidoidThing {

	public static RenderDSL template(String source) {
		return new RenderDSL((RapidoidTemplate) Templates.DEFAULT_FACTORY.compile(source, Object.class));
	}

	public static RenderDSL file(String filename) {
		return file(filename, Templates.DEFAULT_FACTORY);
	}

	public static RenderDSL file(String filename, TemplateFactory factory) {
		return new RenderDSL((RapidoidTemplate) factory.load(filename, Object.class));
	}

}
