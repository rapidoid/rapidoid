package org.rapidoid.integrate;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheResolver;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.View;
import org.rapidoid.http.customize.ResourceLoader;
import org.rapidoid.http.impl.AbstractViewResolver;
import org.rapidoid.u.U;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;

/*
 * #%L
 * rapidoid-integrate
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
public class MustacheJavaViewResolver extends AbstractViewResolver<DefaultMustacheFactory> {

	@Override
	public View getView(String viewName, ResourceLoader resourceLoader) throws Exception {
		String filename = filename(viewName);

		if (resourceLoader.load(filename) == null) return null;

		DefaultMustacheFactory mf = getViewFactory(resourceLoader);

		Mustache mustache = mf.compile(filename);

		return view(mustache);
	}

	@Override
	protected DefaultMustacheFactory createViewFactory(ResourceLoader templateLoader) {
		return new DefaultMustacheFactory(mustacheResolver(templateLoader));
	}

	protected View view(final Mustache mustache) {
		return new View() {
			@Override
			public void render(Object model, OutputStream out) {
				PrintWriter writer = new PrintWriter(out);
				mustache.execute(writer, model);
				writer.flush();
			}
		};
	}

	protected MustacheResolver mustacheResolver(final ResourceLoader templateLoader) {
		return new MustacheResolver() {
			@Override
			public Reader getReader(String name) {
				try {
					byte[] bytes = templateLoader.load(name);
					U.must(bytes != null, "The Mustache.java template '%s' doesn't exist!", name);
					return new StringReader(new String(bytes));
				} catch (Exception e) {
					throw U.rte(e);
				}
			}
		};
	}

}
