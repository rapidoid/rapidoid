package org.rapidoid.pages.bootstrap;

import java.util.List;

import org.rapidoid.html.tag.TbodyTag;
import org.rapidoid.html.tag.TrTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Property;
import org.rapidoid.pages.DynamicContent;
import org.rapidoid.util.U;

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

	private static final long serialVersionUID = -8550720285123228765L;

	public TableWidget(final Items items) {

		final List<Property> properties = items.properties();

		TrTag header = tr();

		for (Property prop : properties) {
			header.append(th(prop.caption()));
		}

		Object body = dynamic(new DynamicContent() {
			@Override
			public Object eval(HttpExchange x) {

				TbodyTag body = tbody();

				for (Item item : items) {
					TrTag row = tr();

					for (Property prop : properties) {
						row.append(td(U.or(item.get(prop.name()), "")));
					}

					body.append(row);
				}

				return body;
			}
		});

		setContent(table_(thead(header), body));
	}

}
