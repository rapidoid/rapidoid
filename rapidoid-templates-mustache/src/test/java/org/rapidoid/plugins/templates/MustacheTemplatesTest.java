package org.rapidoid.plugins.templates;

/*
 * #%L
 * rapidoid-templates-mustache
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

import com.github.mustachejava.MustacheFactory;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class MustacheTemplatesTest extends TestCommons {

	@Test
	public void testFileTemplateComponent() {
		final MustacheFactory factory = new RapidoidMustacheFactory();

		ITemplate template = new MustacheFileTemplate(factory, "main.html");

		eq(template.render(U.map("x", 1), U.map("y", "s")), "A-s[s]B-s[s]:1(1)");
	}

	@Test
	public void testStringTemplateComponent() {
		final MustacheFactory factory = new RapidoidMustacheFactory();

		ITemplate template = new MustacheStringTemplate(factory, "{{x}}-$$y$$");

		eq(template.render(U.map("x", 1), U.map("y", "2")), "1-2");
	}

	@Test
	public void testFileTemplatesAPI() {
		Plugins.register(new MustacheTemplatesPlugin());

		eq(Templates.fromFile("main.html").render(U.map("x", 1), U.map("y", "s")), "A-s[s]B-s[s]:1(1)");
	}

	@Test
	public void testStringTemplatesAPI() {
		Plugins.register(new MustacheTemplatesPlugin());

		eq(Templates.fromString("$$x$$-{{y}}").render(U.map("x", 1), U.map("y", "2")), "1-2");
	}

}
