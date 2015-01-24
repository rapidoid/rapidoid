package org.rapidoid.util;

import java.util.List;

import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

@MyAnnot
class Foo {
}

public class ClasspathScanTest extends TestCommons {

	@SuppressWarnings("unchecked")
	@Test
	public void testClasspathScanByName() {
		List<Class<?>> classes = Scan.classes(null, ".*ScanTest", null, null, null);

		eq(U.set(classes), U.set(ClasspathScanTest.class));

		classes = Scan.byName("AppCtxTest", null, null);

		eq(U.set(classes), U.set(AppCtxTest.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testClasspathScanByAnnotation() {
		List<Class<?>> classes = Scan.annotated(MyAnnot.class);

		eq(U.set(classes), U.set(Foo.class, Bar.class));
	}

}
