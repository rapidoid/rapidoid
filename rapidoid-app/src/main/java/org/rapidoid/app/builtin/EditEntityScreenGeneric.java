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
import org.rapidoid.html.Tag;
import org.rapidoid.plugins.db.DB;
import org.rapidoid.u.U;
import org.rapidoid.widget.FormWidget;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class EditEntityScreenGeneric extends AbstractEntityScreenGeneric {

	private Object entity;

	public EditEntityScreenGeneric(Class<?> entityType) {
		super(entityType);
	}

	public Object content() {
		this.entity = getEntityById();

		Tag caption = h2("Edit " + U.capitalized(ctx().pathSegment(0).substring(4)));
		FormWidget form = edit(entity).buttons(SAVE, CANCEL);

		return mid6(caption, form);
	}

	public void onSave() {
		DB.update(entity);
		ctx().goBack(1);
	}

}
