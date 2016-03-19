package org.rapidoid.util;

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
