package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Session;
import org.rapidoid.annotation.Since;
import org.rapidoid.db.DB;
import org.rapidoid.html.Tag;
import org.rapidoid.security.Secure;
import org.rapidoid.util.Cls;
import org.rapidoid.widget.ButtonWidget;
import org.rapidoid.widget.GridWidget;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class ListEntityScreenGeneric extends Screen {

	private final Class<?> entityType;

	@Session
	public Object newEntity;

	public ListEntityScreenGeneric(Class<?> entityType) {
		this.entityType = entityType;
	}

	public Object content() {

		String entityName = Cls.entityName(entityType);

		Tag caption = titleBox(entityName + " List");
		GridWidget grid = grid(entityType, "-id", 10);

		boolean canAdd = Secure.canInsert(Secure.username(), DB.entity(entityType));
		ButtonWidget btnAdd = canAdd ? btn("Add " + entityName).primary().command("Add") : null;

		return row(caption, grid, btnAdd);
	}

	public void onAdd() {
		throw ctx().redirect("/new" + Cls.entityName(entityType).toLowerCase());
	}

}
