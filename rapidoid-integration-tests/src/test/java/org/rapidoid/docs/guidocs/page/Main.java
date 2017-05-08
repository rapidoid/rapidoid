package org.rapidoid.docs.guidocs.page;

import org.junit.Test;
import org.rapidoid.gui.GUI;
import org.rapidoid.test.Doc;
import org.rapidoid.test.TestCommons;

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
	@Doc(title = "Creating a Bootstrap-based web page", show = false)
	public void docs() {

		/* Creating an embedded renderPage */

		__(GUI.page("Abcde").embedded(true));

	}

}
