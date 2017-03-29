package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.Cache;
import org.rapidoid.cache.Caching;
import org.rapidoid.env.Env;
import org.rapidoid.lambda.Mapper;
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
@Since("5.2.0")
public class RapidoidTemplateFactory extends RapidoidThing implements TemplateFactory {

	private static final int CACHE_TTL = Env.dev() ? 100 : 0;

	public RapidoidTemplate loadAndCompile(String filename) {
		return new RapidoidTemplate(filename, loadTemplate(filename), this);
	}

	private final TemplateStore templateStore;

	private final Cache<String, RapidoidTemplate> compiledTemplates;

	public RapidoidTemplateFactory(String name, TemplateStore templateStore) {
		this.templateStore = templateStore;

		compiledTemplates = Caching.of(new Mapper<String, RapidoidTemplate>() {
			@Override
			public RapidoidTemplate map(String filename) throws Exception {
				return loadAndCompile(filename);
			}
		}).name(name).capacity(10000).ttl(CACHE_TTL).manageable(true).statistics(true).build();
	}

	@Override
	public void reset() {
		compiledTemplates.clear();
	}

	@Override
	public Template load(String filename, Class<?> modelType) {
		return compiledTemplates.get(filename);
	}

	@Override
	public Template compile(String source, Class<?> modelType) {
		return new RapidoidTemplate(null, TemplateParser.parse(source).compile(modelType), this);
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
