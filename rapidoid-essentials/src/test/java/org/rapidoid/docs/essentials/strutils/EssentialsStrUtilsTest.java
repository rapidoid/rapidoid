package org.rapidoid.docs.essentials.strutils;

import org.junit.Test;
import org.rapidoid.test.Doc;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-essentials
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

public class EssentialsStrUtilsTest extends TestCommons {

	@Test
	@Doc(title = "String utils")
	public void docs() {

		/* Is it empty or null: */

		String s = null;
		__(U.isEmpty(s));

		__(U.isEmpty(""));

		__(U.isEmpty("hey"));
	}

}
