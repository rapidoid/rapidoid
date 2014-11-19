package org.rapidoid.demo.pages;

/*
 * #%L
 * rapidoid-demo
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Session;
import org.rapidoid.demo.pojo.Person;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.model.Item;
import org.rapidoid.model.Model;
import org.rapidoid.pages.BootstrapWidgets;

public class BeanFormPage extends BootstrapWidgets {

	@Session
	private Person person = new Person("abc", 22);

	public Tag<?> content(HttpExchange x) {
		ATag brand = a("Edit person").href("/beanForm.html");

		Tag<?>[] buttons = { cmd("Save"), cmd("Cancel") };

		Item item = Model.item(person);

		FormTag form = edit(item, buttons);

		return navbarPage(false, brand, null, arr(rowFull(form), rowFull(person)));
	}

}
