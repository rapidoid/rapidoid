package org.rapidoid.pages.bootstrap;

import java.util.List;

import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Property;
import org.rapidoid.pages.html.TbodyTag;
import org.rapidoid.pages.html.TrTag;

/*
 * #%L
 * rapidoid-pages
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

public class TableWidget extends BootstrapWidget {

	private final List<Property> properties;

	private final Items items;

	public TableWidget(Items items) {
		this.items = items;
		this.properties = items.properties();
	}

	@Override
	protected Object contents() {
		TrTag header = tr();

		for (Property prop : properties) {
			header.append(th(prop.caption()));
		}

		TbodyTag body = tbody();

		for (int i = 0; i < items.size(); i++) {
			TrTag row = tr();
			Item item = items.get(i);

			for (Property prop : properties) {
				row.append(td(item.get(prop.name())));
			}

			body.append(row);
		}

		return table(thead(header), body);
	}

}
