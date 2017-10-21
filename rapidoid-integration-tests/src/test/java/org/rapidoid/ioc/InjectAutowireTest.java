package org.rapidoid.ioc;

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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.IsolatedIntegrationTest;

import javax.inject.Inject;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class InjectAutowireTest extends IsolatedIntegrationTest {

	static class B {
		@Inject
		private MyCallable x;
	}

	@Inject
	private MyCallable myCallable;

	@Inject
	B b1;

	@Wired
	B b2;

	@Test
	public void shouldInject() {
		notNull(myCallable);
		notNull(b1);
		notNull(b2);

		notNull(b1.x);
		notNull(b2.x);

		isTrue(myCallable == b2.x);
		isTrue(b1.x == b2.x);
	}

}
