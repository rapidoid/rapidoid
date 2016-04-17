package org.rapidoid.integrate;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.customize.ViewRenderer;
import org.rapidoid.io.Res;
import org.rapidoid.render.Templates;

import java.io.OutputStream;
import java.io.PrintWriter;

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
@Since("5.1.0")
public class MustacheViewRenderer extends RapidoidThing implements ViewRenderer {

	private final MustacheFactory mf = new DefaultMustacheFactory();

	@Override
	public boolean render(String viewName, Object[] model, OutputStream out) throws Exception {
		Res template = Templates.resource(viewName + ".html");

		if (!template.exists()) {
			return false;
		}

		Mustache mustache = mf.compile(template.getFileName());
		mustache.execute(new PrintWriter(out), model).flush();

		return true;
	}

}
