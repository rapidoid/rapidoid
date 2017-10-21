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

		switch (key) {
			case "app":
				return "label-primary";

			case "on":
				return "label-success";

			case "admin":
			case "token":
				return "label-danger";

			case "users":
			case "tls":
				return "bg-sandy";

			case "jpa":
			case "log":
				return "bg-purple";

			case "hibernate":
			case "reverse-proxy":
				return "bg-sky";

			case "c3p0":
				return "bg-teal";

			case "jdbc":
			case "net":
				return "bg-salmon";

			case "http":
			case "admin-zone":
				return "bg-teal";

			case "jobs":
				return "bg-steel";

			case "oauth":
			case "gui":
				return "bg-pink";

			default:
				return "bg-metal";
		}
	}

}
