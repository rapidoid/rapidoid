package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Coll;
import org.rapidoid.io.Res;
import org.rapidoid.lambda.Mapper;

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

	private static final Map<String, TemplateRenderer> TEMPLATES = Coll.autoExpandingMap(new Mapper<String, TemplateRenderer>() {
		@Override
		public TemplateRenderer map(String name) throws Exception {
			return TemplateParser.parse(resource(name).mustExist().getContent()).compile();
		}
	});

	public static Template fromFile(String filename) {
		return new RapidoidTemplate(filename, TEMPLATES.get(filename));
	}

	public static Template fromString(String source) {
		return new RapidoidTemplate("", TemplateParser.parse(source).compile());
	}

	public static Res resource(String filename) {
		return Res.from(filename, "", "templates", "default/templates");
	}

	public static Template fromRes(Res template) {
		return fromFile(template.getFileName());
	}

}
