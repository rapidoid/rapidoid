package org.rapidoid.widget;

/*
 * #%L
 * rapidoid-widget
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

import java.util.List;
import java.util.Map.Entry;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.3.1")
public class DebugWidget extends AbstractWidget {

	@Override
	protected Object render() {
		List<Object> list = U.list();

		for (Entry<String, Object> e : ctx().session().entrySet()) {
			if (!e.getKey().contains("_")) {
				list.add(div(e.getKey(), " = ", b(e.getValue())));
			}
		}

		return panel(list).header("Session");
	}

}
