package org.rapidoid.docs.layout;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.Page;
import org.rapidoid.gui.GUI;
import org.rapidoid.html.Tag;

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

@Controller
public class BootstrapLayout extends GUI {

	@Page("/")
	public Object layout() {
		Tag r1 = row(col4("A"), col4("B"), col4("C"));
		Tag r2 = row(col1("2/12"), col7("7/12"), col4("3/12"));
		Tag r3 = mid4("4/12 in the middle");
		return multi(r1, r2, r3);
	}

}
