package org.rapidoid.compile;

/*
 * #%L
 * rapidoid-compile
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Set;

import org.rapidoid.log.Log;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.Cls;
import org.rapidoid.util.IO;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class CompileTest extends TestCommons {

	@Test
	public void testCompile() throws Exception {

		Compilation compilation = Compile.compile(IO.load("test1.java"), IO.load("mixin.java"),
				"public class Book { String title; int x = 1234; } class Foo {}", "public class Bar extends Foo {}",
				"public class Fg extends Foo {}");

		Set<Class<?>> classes = compilation.loadClasses();
		eq(classes.size(), 10);

		Set<String> classNames = compilation.getClassNames();
		U.show(classNames);

		eq(Cls.classMap(classes).get("Main").getAnnotations().length, 2);

		Set<String> expectedClasses = U.set("abc.Main", "abc.Main$1", "abc.Person", "abc.Person$Insider",
				"abc.PersonService", "Book", "Foo", "Bar", "Fg", "mixo.Mixin");
		eq(classNames, expectedClasses);

		for (String clsName : expectedClasses) {
			Class<?> cls = compilation.loadClass(clsName);
			notNull(cls);
			eq(cls.getName(), clsName);
		}

		Class<?> mainClass = compilation.loadClass("abc.Main");
		Method main = Cls.getMethod(mainClass, "main", String[].class);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Log.setLogOutput(new PrintStream(out));

		Cls.invoke(main, null, new Object[] { new String[] {} });

		String output = new String(out.toByteArray());
		eq(output, IO.load("test1.out"));
	}

}
