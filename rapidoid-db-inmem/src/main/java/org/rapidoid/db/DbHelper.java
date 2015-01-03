package org.rapidoid.db;

/*
 * #%L
 * rapidoid-db-inmem
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

import org.rapidoid.prop.Prop;
import org.rapidoid.prop.PropertyFilter;

public class DbHelper {

	public static final PropertyFilter DB_REL_PROPS = new PropertyFilter() {
		@Override
		public boolean eval(Prop prop) throws Exception {
			Class<?> type = prop.getType();
			return DbList.class.isAssignableFrom(type) || DbSet.class.isAssignableFrom(type)
					|| DbRef.class.isAssignableFrom(type);
		}
	};

}
