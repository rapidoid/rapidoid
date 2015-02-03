package org.rapidoid.db.impl;

/*
 * #%L
 * rapidoid-db-impl
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
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Prop;
import org.rapidoid.beany.PropertyFilter;
import org.rapidoid.db.DbList;
import org.rapidoid.db.DbRef;
import org.rapidoid.db.DbSet;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DbHelper {

	@SuppressWarnings("serial")
	public static final PropertyFilter DB_REL_PROPS = new PropertyFilter() {
		@Override
		public boolean eval(Prop prop) throws Exception {
			Class<?> type = prop.getType();
			return DbList.class.isAssignableFrom(type) || DbSet.class.isAssignableFrom(type)
					|| DbRef.class.isAssignableFrom(type);
		}
	};

}
