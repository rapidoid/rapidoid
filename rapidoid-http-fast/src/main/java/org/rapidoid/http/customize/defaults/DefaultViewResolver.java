package org.rapidoid.http.customize.defaults;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.View;
import org.rapidoid.http.customize.ResourceLoader;
import org.rapidoid.http.impl.AbstractViewResolver;
import org.rapidoid.render.RapidoidTemplateFactory;
import org.rapidoid.render.Template;
import org.rapidoid.render.TemplateFactory;
import org.rapidoid.render.TemplateStore;
import org.rapidoid.u.U;

import java.io.OutputStream;

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
public class DefaultViewResolver extends AbstractViewResolver<TemplateFactory> {

	@Override
	public View getView(String viewName, final ResourceLoader resourceLoader) throws Exception {
		String filename = filename(viewName);

		if (resourceLoader.load(filename) == null) return null;

		TemplateFactory templateFactory = getViewFactory(resourceLoader);

		Template template = templateFactory.load(filename, Object.class);

		return template != null ? view(template) : null;
	}

	@Override
	protected TemplateFactory createViewFactory(final ResourceLoader templateLoader) {
		return new RapidoidTemplateFactory("views", store(templateLoader));
	}

	protected View view(final Template template) {
		return new View() {
			@Override
			public void render(Object model, OutputStream out) {
				template.renderTo(out, model);
			}
		};
	}

	protected TemplateStore store(final ResourceLoader templateLoader) {
		return new TemplateStore() {
			@Override
			public String loadTemplate(String name) throws Exception {
				byte[] bytes = templateLoader.load(name);
				U.must(bytes != null, "The Rapidoid template '%s' doesn't exist!", name);
				return new String(bytes);
			}
		};
	}

}
