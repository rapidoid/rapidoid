package org.rapidoid.compile;

/*
 * #%L
 * rapidoid-compile
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Set;

import org.rapidoid.test.TestCommons;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.testng.annotations.Test;

public class CompileTest extends TestCommons {

	@Test
	public void testCompile() throws Exception {

		Compilation compilation = Compile.compile(U.load("test1.java"),
				"public class Book { String title; int x = 1234; } class Foo {}", "public class Bar extends Foo {}",
				"public class Fg extends Foo {}");

		System.out.println(compilation);

		Set<Class<?>> classes = compilation.loadClasses();
		U.show(classes);
		eq(classes.size(), 9);

		Set<String> classNames = compilation.getClassNames();
		U.show(classNames);

		Set<String> expectedClasses = U.set("abc.Main", "abc.Main$1", "abc.Person", "abc.Person$Insider",
				"abc.PersonService", "Book", "Foo", "Bar", "Fg");
		eq(classNames, expectedClasses);

		for (String clsName : expectedClasses) {
			Class<?> cls = compilation.loadClass(clsName);
			notNull(cls);
			eq(cls.getName(), clsName);
		}

		Class<?> mainClass = compilation.loadClass("abc.Main");
		Method main = UTILS.getMethod(mainClass, "main", String[].class);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		U.setLogOutput(new PrintStream(out));

		UTILS.invoke(main, null, new Object[] { new String[] {} });

		String output = new String(out.toByteArray());
		eq(output, U.load("test1.out"));
	}

}
