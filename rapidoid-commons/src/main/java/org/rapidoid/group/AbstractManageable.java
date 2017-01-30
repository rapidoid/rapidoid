package org.rapidoid.group;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Str;
import org.rapidoid.u.U;

import java.lang.reflect.Method;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public abstract class AbstractManageable extends RapidoidThing implements Manageable {

	@Override
	public Object execute(String action) {
		Method method = Cls.findMethod(getClass(), Str.uncapitalized(action));

		if (method != null) {
			return Cls.invoke(method, this);

		} else {
			return executeAction(action);
		}
	}

	protected Object executeAction(String action) {
		throw U.rte("Cannot handle action '%s'!", action);
	}

}
