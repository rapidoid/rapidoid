package custom;

/*
 * #%L
 * rapidoid-demo
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

import org.rapidoid.db.impl.inmem.DbImpl;
import org.rapidoid.log.Log;

public class CustomizedDbImpl extends DbImpl {

	private static final long serialVersionUID = -3304900771653853896L;

	public CustomizedDbImpl(String name, String filename) {
		super(name, filename);
	}

	@Override
	public void delete(long id) {
		Log.warn("deleting record", "id", id);
		super.delete(id);
	}

}
