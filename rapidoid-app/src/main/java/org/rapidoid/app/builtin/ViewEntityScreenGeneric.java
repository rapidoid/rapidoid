package org.rapidoid.app.builtin;

/*
 * #%L
 * rapidoid-app
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
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.gui.ButtonWidget;
import org.rapidoid.gui.FormWidget;
import org.rapidoid.html.Tag;
import org.rapidoid.plugins.db.DB;
import org.rapidoid.security.Secure;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class ViewEntityScreenGeneric extends AbstractEntityScreenGeneric {

	private Object entity;

	public ViewEntityScreenGeneric(Class<?> entityType) {
		super(entityType);
	}

	public Object content() {
		this.entity = getEntityById();

		Tag caption = h2(U.capitalized(ctx().pathSegment(0)) + " Details").style("margin-bottom:15px;");
		FormWidget details = show(entity);

		Ctx ctx = Ctxs.ctx();
		ButtonWidget btnEdit = Secure.canUpdate(ctx.username(), entity) ? EDIT : null;
		ButtonWidget btnDelete = Secure.canDelete(ctx.username(), entity) ? DELETE : null;

		details = details.buttons(btnEdit, BACK, btnDelete);

		return mid6(caption, details);
	}

	public void onEdit() {
		String id = ctx().pathSegment(1);
		ctx().redirect("/edit" + Cls.entityName(entity).toLowerCase() + "/" + id);
	}

	public void onDelete() {
		// TODO ask h4("Are you sure you want to delete the record?"), div(YES_DELETE, CANCEL));
		String id = ctx().pathSegment(1);
		DB.delete(entityType, id);
		ctx().goBack(1);
	}

}
