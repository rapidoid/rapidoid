package org.rapidoid.html.impl;

/*
 * #%L
 * rapidoid-html
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

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.rapidoid.html.SpecificTagBase;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagBase;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class TagProxy implements InvocationHandler, Serializable {

	private static final long serialVersionUID = 8876053750757191711L;

	public static <TAG extends Tag> TAG create(Class<?> tagInterface, String tagName, Object[] contents) {
		TagImpl tag = new TagImpl(tagInterface, tagName, contents);

		TAG proxy = Cls.createProxy(new TagProxy(tag, tagInterface), tagInterface, TagInternals.class);
		tag.setProxy(proxy);

		return proxy;
	}

	private final TagImpl tag;

	private final Class<?> clazz;

	public TagProxy(TagImpl tag, Class<?> clazz) {
		this.tag = tag;
		this.clazz = clazz;
	}

	public Object invoke(Object target, Method method, Object[] args) throws Throwable {

		Class<?> methodClass = method.getDeclaringClass();

		String name = method.getName();
		Class<?> ret = method.getReturnType();
		Class<?>[] paramTypes = method.getParameterTypes();

		Method m2 = Cls.findMethod(SpecificTagBase.class, name, paramTypes);
		if (methodClass.equals(Object.class) || methodClass.equals(TagBase.class)
				|| methodClass.equals(TagInternals.class) || m2 != null) {
			return method.invoke(tag, args);
		}

		boolean returnsTag = ret.isAssignableFrom(clazz);
		boolean returnsStr = ret.equals(String.class);
		boolean returnsBool = ret.equals(boolean.class);

		boolean has0arg = paramTypes.length == 0;
		boolean has1arg = paramTypes.length == 1;

		// String attribute setter
		if (returnsTag && has1arg && paramTypes[0].equals(String.class)) {
			return tag.attr(attr(name), (String) args[0]);
		}

		// String attribute getter
		if (returnsStr && has0arg) {
			return tag.attr(attr(name));
		}

		// boolean attribute setter
		if (returnsTag && has1arg && paramTypes[0].equals(boolean.class)) {
			return tag.is(attr(name), (Boolean) args[0]);
		}

		// boolean attribute getter
		if (returnsBool && has0arg) {
			return tag.is(attr(name));
		}

		// extra attribute setter
		if (returnsTag && has1arg) {
			return tag.extra(attr(name), args[0]);
		}

		// in case something is missed
		throw U.rte("Not implemented: " + name);
	}

	private static String attr(String name) {
		return name.endsWith("_") ? U.mid(name, 0, -1) : name;
	}

}
