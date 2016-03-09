package org.rapidoid.goodies;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
import org.rapidoid.config.Conf;
import org.rapidoid.gui.GUI;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ConfigHandler extends GUI implements Callable<Object> {

	@Override
	public Object call() throws Exception {

		List<Object> grids = U.list();
		Map<String, Object> sections = U.cast(Conf.ROOT.toMap());

		for (Map.Entry<String, Object> entry : sections.entrySet()) {
			String key = entry.getKey();
			Object section = entry.getValue();

			grids.add(h4(span(key).class_("label label-" + styleOf(key))));

			if (section instanceof Map<?, ?>) {
				grids.add(grid((Map<?, ?>) section));
			} else {
				grids.add(div(section));
			}
		}

		return multi(grids.toArray());
	}

	private String styleOf(String key) {
		if (U.eq(key, "app")) {
			return "primary";

		} else if (U.eq(key, "dev")) {
			return "success";

		} else if (U.eq(key, "admin")) {
			return "danger";

		} else if (U.eq(key, "dev")) {
			return "success";

		} else if (U.eq(key, "users")) {
			return "warning";

		} else {
			return "default";
		}
	}

}
