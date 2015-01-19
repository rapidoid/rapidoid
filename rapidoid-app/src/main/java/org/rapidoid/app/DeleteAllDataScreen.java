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

import org.rapidoid.db.DB;
import org.rapidoid.html.Tag;
import org.rapidoid.log.Log;
import org.rapidoid.security.annotation.DevMode;

@DevMode
public class DeleteAllDataScreen extends Screen {

	public Object content() {
		Tag caption = titleBox("Debug Mode - Delete All data");
		return div(caption, div(btnDanger("DELETE ALL DATA!").cmd("DeleteAll"), CANCEL));
	}

	public void onDeleteAll() {
		showModal("confirmDelete");
	}

	public Tag confirmDelete() {
		return modal("Confirm data deletion", h1("Are you sure you want to delete all data in the database?"),
				div(YES_DELETE, NO));
	}

	public void onYesDelete() {
		Log.info("yes");
		hideModal();
		DB.clear();
	}

	public void onNo() {
		hideModal();
	}

}
