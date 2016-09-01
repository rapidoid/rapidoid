package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.lambda.Mapper;
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
@Since("5.2.0")
public class RapidoidTemplateFactory extends RapidoidThing implements TemplateFactory {

	private final Map<String, RapidoidTemplate> compiledTemplates = Coll.autoExpandingMap(new Mapper<String, RapidoidTemplate>() {
		@Override
		public RapidoidTemplate map(String filename) throws Exception {
			return loadAndCompile(filename);
		}
	});

	public RapidoidTemplate loadAndCompile(String filename) {
		return new RapidoidTemplate(filename, loadTemplate(filename), this);
	}

	private final TemplateStore templateStore;

	public RapidoidTemplateFactory(TemplateStore templateStore) {
		this.templateStore = templateStore;
	}

	@Override
	public void reset() {
		compiledTemplates.clear();
	}

	@Override
	public Template load(String filename) {
		return compiledTemplates.get(filename);
	}

	@Override
	public Template compile(String source) {
		return new RapidoidTemplate(null, TemplateParser.parse(source).compile(), this);
	}

	protected TemplateRenderer loadTemplate(String filename) {
		String content;

		try {
			content = templateStore.loadTemplate(filename);
		} catch (Exception e) {
			throw U.rte("Couldn't load template: " + filename, e);
		}

		return TemplateParser.parse(content).compile();
	}

}
