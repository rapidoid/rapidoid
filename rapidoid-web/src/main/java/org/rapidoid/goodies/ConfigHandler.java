package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.gui.FA;
import org.rapidoid.gui.GUI;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ConfigHandler extends GUI implements Callable<Object> {

	@SuppressWarnings("unchecked")
	@Override
	public Object call() throws Exception {

		List<Object> grids = U.list();
		Map<String, Object> sections = U.cast(Conf.ROOT.toMap());
		sections = Msc.protectSensitiveInfo(sections, FA.QUESTION_CIRCLE);

		Map<String, Object> root = U.map();

		for (Map.Entry<String, Object> entry : sections.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (value instanceof Map<?, ?>) {
				grids.add(h4(span(key).class_("label " + styleOf(key))));
				grids.add(grid((Map<String, ?>) value));
			} else {
				root.put(key, value);
			}
		}

		if (!root.isEmpty()) {
			grids.add(0, h4(span("<root>").class_("label " + styleOf("root"))));
			grids.add(1, grid(root));
		}

		return multi(grids);
	}

	private String styleOf(String key) {
		if (U.eq(key, "app")) {
			return "label-primary";

		} else if (U.eq(key, "on")) {
			return "label-success";

		} else if (U.eq(key, "admin")) {
			return "label-danger";

		} else if (U.eq(key, "users")) {
			return "bg-sandy";

		} else if (U.eq(key, "jpa")) {
			return "bg-purple";

		} else if (U.eq(key, "hibernate")) {
			return "bg-sky";

		} else if (U.eq(key, "c3p0")) {
			return "bg-teal";

		} else if (U.eq(key, "jdbc")) {
			return "bg-salmon";

		} else if (U.eq(key, "token")) {
			return "bg-purple";

		} else if (U.eq(key, "http")) {
			return "bg-teal";

		} else if (U.eq(key, "jobs")) {
			return "bg-steel";

		} else if (U.eq(key, "oauth")) {
			return "bg-pink";

		} else {
			return "bg-metal";
		}
	}

}
