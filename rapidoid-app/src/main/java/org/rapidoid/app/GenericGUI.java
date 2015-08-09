package org.rapidoid.app;

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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class GenericGUI {

	private static final String PAGE_RELOAD = "<h2>&nbsp;Reloading...</h2><script>location.reload();</script>";

	public static Object genericScreen() {

		// TODO refactor and enable this

		// String path = x.path();
		//
		// if (path.equals("/")) {
		// return appCls.main != null ? app : new Object();
		// }
		//
		// for (Class<?> scr : BUILT_IN_SCREENS) {
		// if (Apps.screenUrl(scr).equals(path)) {
		// return Cls.newInstance(scr);
		// }
		// }
		//
		// if (!x.query().isEmpty()) {
		// return null;
		// }
		//
		// Matcher m = ENTITY_EDIT.matcher(path);
		//
		// if (m.find()) {
		// String type = m.group(1);
		// String id = m.group(2);
		//
		// Class<?> entityType = Scaffolding.getScaffoldingEntity(type);
		// if (entityType == null) {
		// return null;
		// }
		//
		// Object entity = DB.getIfExists(entityType, id);
		//
		// String entityClass = Cls.entityName(entity);
		// String reqType = U.capitalized(type);
		//
		// if (entityClass.equals(reqType)) {
		// return new EditEntityScreenGeneric(entityType);
		// }
		// }
		//
		// m = ENTITY_NEW.matcher(path);
		//
		// if (m.find()) {
		// String type = m.group(1);
		//
		// Class<?> entityType = Scaffolding.getScaffoldingEntity(type);
		// if (entityType == null) {
		// return null;
		// }
		//
		// return new NewEntityScreenGeneric(entityType);
		// }
		//
		// m = ENTITY_VIEW.matcher(path);
		//
		// if (m.find()) {
		// String type = m.group(1);
		// String id = m.group(2);
		//
		// Class<?> entityType = Scaffolding.getScaffoldingEntity(type);
		// if (entityType == null) {
		// return null;
		// }
		//
		// Object entity = DB.getIfExists(entityType, id);
		//
		// String entityClass = Cls.entityName(entity);
		// String reqType = U.capitalized(type);
		//
		// if (entityClass.equals(reqType)) {
		// return new ViewEntityScreenGeneric(entityType);
		// }
		// }
		//
		// m = ENTITY_LIST.matcher(path);
		//
		// if (m.find()) {
		// String type = m.group(1);
		// String type2 = U.or(Languages.pluralToSingular(type), type);
		//
		// Class<?> entityType = Scaffolding.getScaffoldingEntity(type2);
		// if (entityType == null) {
		// return null;
		// }
		//
		// return new ListEntityScreenGeneric(entityType);
		// }
		//
		// return null;

		return null;
	}

	public static final void reload(HttpExchange x) {
		Map<String, String> sel = U.map("body", PAGE_RELOAD);
		x.writeJSON(U.map("_sel_", sel));
	}

}
