package org.rapidoid.reload;

/*
 * #%L
 * rapidoid-watch
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
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Set;

public class ClassReloaderTest extends TestCommons {

	@Test
	public void testReloading() throws ClassNotFoundException {
		Set<String> classpath = ClasspathUtil.getClasspathFolders();
		List<String> names = U.list();

		ClassReloader reloader = new ClassReloader(classpath, ClassReloaderTest.class.getClassLoader(), names);

		Class<?> foo1 = reloader.loadClass(FooClass.class.getName());
		Class<?> foo2 = reloader.loadClass(FooClass.class.getName());

		eq(foo1, foo2);
	}

}
