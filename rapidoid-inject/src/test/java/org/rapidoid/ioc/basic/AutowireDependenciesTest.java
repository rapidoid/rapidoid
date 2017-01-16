package org.rapidoid.ioc.basic;

/*
 * #%L
 * rapidoid-inject
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
import org.rapidoid.ioc.Manage;
import org.rapidoid.ioc.Wired;
import org.rapidoid.test.RapidoidIntegrationTest;

import javax.inject.Inject;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
@Manage(MyCallable.class)
public class AutowireDependenciesTest extends RapidoidIntegrationTest {

	static class B {
		@Inject
		private Foo foo;
	}

	@Inject
	private Foo foo;

	@Inject
	Callable<?> callable;

	@Inject
	Callable<?> callable2;

	@Inject
	B b1;

	@Wired
	B b2;

	@Test
	public void shouldInjectManagedDependencies() {
		notNull(foo);
		notNull(b1);
		notNull(b2);

		notNull(b1.foo);
		notNull(b2.foo);

		notNull(b1.foo.callable);
		notNull(b2.foo.callable);

		isTrue(foo == b2.foo);
		isTrue(b1.foo == b2.foo);

		isTrue(foo.callable == callable);
		isTrue(callable == callable2);
	}

}
