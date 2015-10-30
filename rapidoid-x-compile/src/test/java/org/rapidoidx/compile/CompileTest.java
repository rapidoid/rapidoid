package org.rapidoidx.compile;

/*
 * #%L
 * rapidoid-x-compile
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.lang.reflect.Method;
import java.util.Set;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.io.IO;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.D;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class CompileTest extends TestCommons {

	@Test
	public void testCompile() throws Exception {

		Compilation compilation = Compile.compile(IO.load("test1.java"), IO.load("mixin.java"),
				"public class Book { String title; int x = 1234; } class Foo {}", "public class Bar extends Foo {}",
				"public class Fg extends Foo {}");

		System.out.println("ERRORS:" + compilation.getErrors());
		isTrue(compilation.getErrors().isEmpty());

		Set<Class<?>> classes = compilation.loadClasses();
		eq(classes.size(), 10);

		Set<String> classNames = compilation.getClassNames();
		D.print(classNames);

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

		Cls.invoke(main, null, new Object[] { new String[] {} });
	}

}
