package org.rapidoid.util;

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

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.junit.Test;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

import java.io.OutputStream;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RapidoidThingsTest {

	@Test
	public void classesShouldExtendRapidoidThing() {
		for (String cls : Cls.getRapidoidClasses()) {

			if (cls.startsWith("org.rapidoid.plugin.app.")
				|| cls.startsWith("org.rapidoid.fluent.")
				|| cls.startsWith("org.rapidoid.benchmark.")) continue;

			Class<?> clazz = Cls.get(cls);

			if (!clazz.isInterface() && !clazz.isEnum() && !clazz.isAnnotation()) {
				U.must(RapidoidThing.class.isAssignableFrom(clazz)
					|| clazz == TestCommons.class
					|| Exception.class.isAssignableFrom(clazz)
					|| ClassLoader.class.isAssignableFrom(clazz)
					|| HibernatePersistenceProvider.class.isAssignableFrom(clazz)
					|| OutputStream.class.isAssignableFrom(clazz)
					|| Map.class.isAssignableFrom(clazz)
					|| JsonSerializer.class.isAssignableFrom(clazz)
					|| JsonDeserializer.class.isAssignableFrom(clazz)
					|| LogFactory.class.isAssignableFrom(clazz)
					|| Thread.class.isAssignableFrom(clazz), "" + cls);
			}
		}
	}

}
