package org.rapidoid.docs.essentials.collutils;

import org.junit.Test;
import org.rapidoid.test.Doc;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

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

public class EssentialsUtilsTest extends TestCommons {

	@Test
	@Doc(title = "Collection utils")
	public void docs() {

		List<Integer> words = __(U.list(10, 20, 30));

		Map<Integer, String> nums = __(U.map(1, "one", 5, "five"));

		/* U.isEmpty utils: */

		__(U.isEmpty(words));

		__(U.isEmpty(nums));
	}

}
