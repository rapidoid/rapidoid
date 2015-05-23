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
import org.rapidoid.html.Tag;
import org.rapidoid.plugins.DB;
import org.rapidoid.security.Secure;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;
import org.rapidoid.widget.ButtonWidget;
import org.rapidoid.widget.FormWidget;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class ViewEntityScreenGeneric extends AbstractEntityScreenGeneric {

	public ViewEntityScreenGeneric(Class<?> entityType) {
		super(entityType);
	}

	@Session
	private Object target;

	public Object content() {
		target = entity();

		Tag caption = h2(U.capitalized(ctx().pathSegment(0)) + " Details").style("margin-bottom:15px;");
		FormWidget details = show(target);

		ButtonWidget btnEdit = Secure.canUpdate(Secure.username(), target) ? EDIT : null;
		ButtonWidget btnDelete = Secure.canDelete(Secure.username(), target) ? DELETE : null;

		details = details.buttons(btnEdit, BACK, btnDelete);

		return mid6(caption, details);
	}

	public void onEdit() {
		String id = ctx().pathSegment(1);
		ctx().redirect("/edit" + Cls.entityName(target).toLowerCase() + "/" + id);
	}

	public void onDelete() {
		showModal("confirmDeletion");
	}

	public void onYesDelete() {
		long id = Long.parseLong(ctx().pathSegment(1));
		DB.delete(entityType, id);

		hideModal();
		ctx().goBack(1);
	}

	public Tag confirmDeletion() {
		return modal("Confirm deletion", h4("Are you sure you want to delete the record?"), div(YES_DELETE, CANCEL));
	}

}
