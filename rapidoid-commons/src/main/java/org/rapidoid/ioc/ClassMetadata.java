package org.rapidoid.ioc;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.*;
import org.rapidoid.cls.Cls;
import org.rapidoid.util.Msc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ClassMetadata {

	final Class<?> clazz;

	final List<Field> injectableFields;

	final List<Field> sessionFields;

	final List<Field> localFields;

	public ClassMetadata(Class<?> clazz) {
		this.clazz = clazz;
		this.injectableFields = Cls.getFieldsAnnotated(clazz, Inject.class);
		this.sessionFields = Cls.getFieldsAnnotated(clazz, Session.class);
		this.localFields = Cls.getFieldsAnnotated(clazz, Local.class);

		if (Msc.hasInject()) {
			Class<Annotation> javaxInject = Cls.get("javax.inject.Inject");
			this.injectableFields.addAll(Cls.getFieldsAnnotated(clazz, javaxInject));
		}
	}

}
