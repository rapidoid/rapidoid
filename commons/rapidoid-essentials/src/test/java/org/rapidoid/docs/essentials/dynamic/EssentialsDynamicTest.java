package org.rapidoid.docs.essentials.dynamic;

import org.junit.Test;
import org.rapidoid.test.Doc;
import org.rapidoid.test.TestCommons;

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

public class EssentialsDynamicTest extends TestCommons {

	@Test
	@Doc(title = "Dynamic implementation of interfaces", show = false)
	public void docs() {

		// Dog dog = U.dynamic(Dog.class, (m, margs) -> m.getName() + ":" + margs[0]);

//		__(dog.bite("me"));

//		__(dog.eat("meat"));
	}

}
