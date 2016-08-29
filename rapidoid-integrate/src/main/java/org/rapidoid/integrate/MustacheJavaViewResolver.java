package org.rapidoid.integrate;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.View;
import org.rapidoid.http.customize.ViewResolver;
import org.rapidoid.render.TemplateStore;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;

/*
 * #%L
 * rapidoid-integrate
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
public class MustacheJavaViewResolver extends RapidoidThing implements ViewResolver {

	private volatile MustacheFactory factory = new DefaultMustacheFactory();

	@Override
	public View getView(String viewName, TemplateStore templates) throws Exception {

		String filename = viewName + ".html";
		final String template = templates.loadTemplate(filename);

		final Mustache mustache = factory.compile(new StringReader(template), filename);

		return new View() {
			@Override
			public void render(Object model, OutputStream out) {
				PrintWriter writer = new PrintWriter(out);
				mustache.execute(writer, model);
				writer.flush();
			}
		};
	}

	public MustacheFactory factory() {
		return factory;
	}

	public MustacheJavaViewResolver factory(MustacheFactory factory) {
		this.factory = factory;
		return this;
	}
}
