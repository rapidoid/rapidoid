package org.rapidoid.app;

import java.util.Comparator;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.model.Items;
import org.rapidoid.model.impl.DbItems;
import org.rapidoid.pages.PageGUI;
import org.rapidoid.plugins.Entities;
import org.rapidoid.widget.GridWidget;

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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class AppGUI extends PageGUI {

	public static <T> Items all(Class<T> type) {
		return new DbItems<T>(type, null, Beany.<T> comparator("id"));
	}

	public static <T> Items all(Class<T> type, String orderBy) {
		return new DbItems<T>(type, null, Beany.<T> comparator(orderBy));
	}

	public static <T> Items all(Class<T> type, Predicate<T> match, String orderBy) {
		return new DbItems<T>(type, match, Beany.<T> comparator(orderBy));
	}

	public static <T> Items all(Class<T> type, Predicate<T> match, Comparator<T> orderBy) {
		return new DbItems<T>(type, match, orderBy);
	}

	@SuppressWarnings("unchecked")
	public static <T> Items all(Predicate<T> match, Comparator<T> orderBy) {
		return new DbItems<T>((Class<T>) Object.class, match, orderBy);
	}

	public static <T> Items all(Predicate<T> match) {
		return all(match, Beany.<T> comparator("id"));
	}

	public static <T> GridWidget grid(Class<T> type, String sortOrder, int pageSize, String... properties) {
		return grid(all(type, sortOrder), sortOrder, pageSize, properties);
	}

	public static <T> GridWidget grid(Class<T> type) {
		return grid(all(type, ""), "", 10);
	}

	public static <T> GridWidget grid(String type) {
		return grid(Entities.getEntityType(type));
	}

	public static <T> GridWidget grid(Predicate<T> match) {
		return grid(all(match), "id", 10, new String[0]);
	}

	public static <T> GridWidget grid(Class<T> type, Predicate<T> match, String sortOrder, int pageSize,
			String... properties) {
		return grid(all(type, match, sortOrder), sortOrder, pageSize, properties);
	}

	public static <T> GridWidget grid(Class<T> type, Predicate<T> match, Comparator<T> orderBy, int pageSize,
			String... properties) {
		return grid(all(type, match, orderBy), null, pageSize, properties);
	}

}
