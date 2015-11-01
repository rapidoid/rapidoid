package org.rapidoid.app.builtin;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import org.rapidoid.cls.Cls;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.gui.ButtonWidget;
import org.rapidoid.gui.GridWidget;
import org.rapidoid.html.Tag;
import org.rapidoid.plugins.entities.Entities;
import org.rapidoid.security.Secure;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class ListEntityScreenGeneric extends AbstractEntityScreenGeneric {

	@Session
	public Object newEntity;

	public ListEntityScreenGeneric(Class<?> entityType) {
		super(entityType);
	}

	public Object content() {

		String entityName = Cls.entityName(entityType);

		Tag caption = titleBox(entityName + " List");
		GridWidget grid = grid(entityType, 10);

		boolean canAdd = Secure.canInsert(Ctxs.ctx().username(), Entities.create(entityType));
		ButtonWidget btnAdd = canAdd ? btn("Add " + entityName).primary().command("Add") : null;

		return row(caption, grid, btnAdd);
	}

	public void onAdd() {
		throw ctx().redirect("/new" + Cls.entityName(entityType).toLowerCase());
	}

}
