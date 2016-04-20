package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

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
@Since("5.1.0")
public class RapidoidTemplate extends RapidoidThing implements Template {

	private final String filename;

	private final TemplateRenderer template;

	public RapidoidTemplate(String filename, TemplateRenderer template) {
		this.filename = filename;
		this.template = template;
	}

	public void renderTo(OutputStream output, Object... scopes) {
		template.render(new RenderCtxImpl(output, filename, scopes));
	}

	@Override
	public String render(Object... scopes) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		renderTo(out, scopes);
		return out.toString();
	}

}
