package org.rapidoid.docs.guidocs.mapgrid;

import org.junit.Test;
import org.rapidoid.gui.GUI;
import org.rapidoid.test.Doc;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

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

public class Main extends TestCommons {

	@Test
	@Doc(title = "Creating a Two-column Grid from a Map")
	public void docs() {

		Map<Integer, String> nums = __(U.map(1, "one", 5, "five"));

		/* Creating a grid (table) from the [nums] map */

		__(GUI.grid(nums));

		/* Custom table headers */

		__(GUI.grid(nums).headers("Number", "As Text"));

		/* Custom views for the keys and values */

		__(GUI.grid(nums)
			.keyView(k -> GUI.b(k))
			.valueView(v -> v + "!"));
	}

}
