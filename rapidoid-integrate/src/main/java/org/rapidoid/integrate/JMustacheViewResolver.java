package org.rapidoid.integrate;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.View;
import org.rapidoid.http.customize.ViewResolver;
import org.rapidoid.render.TemplateStore;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
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

/**
 * <p>ViewResolver for samskivert's JMustache. <br>
 * To use this call {@code My.viewResolver(new JMustacheViewResolver());} </p>
 * <p>If you want to change any compiler configurations or add partial support do e.g.:</p>
 * <pre>{@code
 *    JMustacheViewResolver resolver = ...;
 *    resolver.setCompiler(Mustache.compiler().defaultValue("Template not found!"));
 * }</pre>
 */
@Authors({"kormakur", "Nikolche Mihajlovski"})
@Since("5.2.0")
public class JMustacheViewResolver extends RapidoidThing implements ViewResolver {

	private volatile Mustache.Compiler compiler = Mustache.compiler();

	@Override
	public View getView(String viewName, final TemplateStore templates) throws Exception {

		final String template = templates.loadTemplate(viewName + ".html");

		Mustache.TemplateLoader loader = new Mustache.TemplateLoader() {
			@Override
			public Reader getTemplate(String name) throws Exception {
				return new StringReader(templates.loadTemplate(name + ".html"));
			}
		};

		final Template mustache = compiler.withLoader(loader).compile(template);

		return new View() {
			@Override
			public void render(Object model, OutputStream out) {
				PrintWriter writer = new PrintWriter(out);
				mustache.execute(model, writer);
				writer.flush();
			}
		};
	}

	/**
	 * Change the compiler settings e.g.
	 * <pre>
	 *     {@code
	 *        resolver.setCompiler(Mustache.compiler().defaultValue("Template not found"));
	 *     }
	 * </pre>
	 *
	 * @param compiler Usually Mustache.compiler() with supplied settings.
	 */
	public synchronized void setCompiler(Mustache.Compiler compiler) {
		this.compiler = compiler;
	}

}
