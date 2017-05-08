package org.rapidoid.docs.guidocs.mapedit;

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
	@Doc(title = "Creating a form from a Map")
	public void docs() {
		Map<String, Object> movie = __(U.map("Title", "Hackers", "cool", true));

		/* Creating a form to edit the [movie] map */

		__(GUI.edit(movie));

		/* Adding form buttons */

		__(GUI.edit(U.map("Title", "")).buttons(GUI.btn("OK")));

	}

}
