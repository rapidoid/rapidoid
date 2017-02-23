package org.rapidoid.integrate;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
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

/**
 * <p>ViewResolver for samskivert's JMustache. <br>
 * To use this call {@code My.viewResolver(new JMustacheViewResolver());} </p>
 * <p>If you want to customize any compiler configurations do e.g.:</p>
 * <pre>{@code
 *    JMustacheViewResolver resolver = new JMustacheViewResolver();
 *    resolver.setCustomizer(compiler -> compiler.defaultValue("N/A"));
 * }</pre>
 */
@Authors({"kormakur", "Nikolche Mihajlovski"})
@Since("5.2.0")
public class JMustacheViewResolver extends AbstractViewResolver<Mustache.Compiler> {

	@Override
	public View getView(String viewName, final ResourceLoader resourceLoader) throws Exception {
		String filename = filename(viewName);

		byte[] bytes = resourceLoader.load(filename);
		if (bytes == null) return null;

		Mustache.TemplateLoader loader = loader(resourceLoader);

		String template = new String(bytes);

		Mustache.Compiler compiler = getViewFactory(resourceLoader);

		Template mustache = compiler.withLoader(loader).compile(template);

		return view(mustache);
	}

	@Override
	protected Mustache.Compiler createViewFactory(ResourceLoader templateLoader) {
		return Mustache.compiler().withLoader(loader(templateLoader));
	}

	protected Mustache.TemplateLoader loader(final ResourceLoader templateLoader) {
		return new Mustache.TemplateLoader() {
			@Override
			public Reader getTemplate(String name) throws Exception {
				String filename = filename(name);
				byte[] bytes = templateLoader.load(filename);
				U.must(bytes != null, "The JMustache template '%s' doesn't exist!", filename);
				return new StringReader(new String(bytes));
			}
		};
	}

	protected View view(final Template mustache) {
		return new View() {
			@Override
			public void render(Object model, OutputStream out) {
				PrintWriter writer = new PrintWriter(out);
				mustache.execute(model, writer);
				writer.flush();
			}
		};
	}
}
