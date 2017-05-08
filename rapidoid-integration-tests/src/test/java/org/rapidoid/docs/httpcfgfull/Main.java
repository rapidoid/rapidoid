package org.rapidoid.docs.httpcfgfull;

import org.rapidoid.fluent.Do;
import org.rapidoid.fluent.Find;
import org.rapidoid.fluent.Flow;
import org.rapidoid.gui.Btn;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.KVGrid;
import org.rapidoid.http.Req;
import org.rapidoid.log.Log;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

/*
 * #%L
 * rapidoid-integration-tests
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

public class Main extends GUI {

	public static void main(String[] args) {

		App.run(args);

	/* The application will manage this configuration: */

		Map<String, String> cfg = U.map("title", "App", "user", "u1");

	/* RESTful service for configuration retrieval */

		On.get("/cfg").json(() -> cfg);

		Map<String, String> menu = U.map("Home", "/", "Group", "/group");

	/* Wrap every result into a renderPage */

		On.defaults().wrappers((data, next) ->
			next.invokeAndTransformResult(x -> {
				return page(x).menu(menu).brand(cfg.get("title"));
			})
		);

	/* Create buttons to filter by starting letter */

		List<Btn> letters = Flow.chars('a', 'z').map(c -> {
			return btn(c).go("/find?p=" + c);
		}).toList();

	/* Display the configuration entries at the home renderPage */

		On.page("/").mvc((Req req) -> {
			KVGrid grid = grid(cfg).keyView(k -> a(k).href("/edit?k=" + k));
			return multi(letters, grid);
		});

	/* Group the configuration entries by the starting letter */

		On.page("/group").mvc((Req req) -> {
			return multi(Do.map(Do.group(cfg).by((k, v) -> k.charAt(0)))
				.toList((k, v) -> U.list(h3(k), grid(v))));
		});

	/* Edit configuration entries */

		On.page("/edit").mvc((String k) -> {
			Btn ok = btn("Update").onClick(() -> Log.info("Updated!"));
			return edit(cfg, k).buttons(ok, btn("Back").go("/"));
		});
	
	/* Search configuration entries by prefix */

		On.page("/find").mvc((String p) -> {
			return grid(Find.allOf(cfg).where((k, v) -> k.startsWith(p)));
		});
	}
}
