package org.rapidoid.ioc;

import org.rapidoid.annotation.*;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Coll;
import org.rapidoid.lambda.F3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ClassMetadata {

	final Class<?> clazz;

	final List<Field> injectableFields;

	final List<Field> sessionFields;

	final List<Field> localFields;

	final Set<Object> injectors = Coll.synchronizedSet();

	final List<F3<Object, Object, Method, Object[]>> interceptors = Coll.synchronizedList();

	public ClassMetadata(Class<?> clazz) {
		this.clazz = clazz;
		this.injectableFields = Cls.getFieldsAnnotated(clazz, Inject.class);
		this.sessionFields = Cls.getFieldsAnnotated(clazz, Session.class);
		this.localFields = Cls.getFieldsAnnotated(clazz, Local.class);
	}

}
