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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.rapidoid.html.Action;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagEventHandler;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class TagInterceptor<TAG extends Tag<?>> implements InvocationHandler {

	public static <TAG extends Tag<?>> TAG create(Class<TAG> clazz, String tagName, Object[] contents) {
		TagData<TAG> tagData = new TagData<TAG>(clazz, tagName, contents);
		TAG tagImpl = Cls.implement(tagData, new TagInterceptor<TAG>(tagData, clazz), clazz, TagInternals.class);
		tagData.setTag(tagImpl);
		return tagImpl;
	}

	private final TagData<TAG> tag;

	private final Class<?> clazz;

	public TagInterceptor(TagData<TAG> tag, Class<TAG> clazz) {
		this.tag = tag;
		this.clazz = clazz;
	}

	@SuppressWarnings("unchecked")
	public Object invoke(Object target, Method method, Object[] args) throws Throwable {

		String name = method.getName();
		Class<?> ret = method.getReturnType();
		Class<?>[] paramTypes = method.getParameterTypes();

		// str
		if (name.equals("str") && paramTypes.length == 1 && paramTypes[0].equals(int.class)) {
			return TagRenderer.get().str(tag, (Integer) args[0], false, null);
		}

		// tagData()
		if (name.equals("tagData") && paramTypes.length == 0) {
			return tag;
		}

		// setHnd(String hnd)
		if (name.equals("setHnd") && paramTypes.length == 1 && paramTypes[0].equals(String.class)) {
			tag.setHnd((String) args[0]);
			return null;
		}

		// // action
		// if (Handler.class.isAssignableFrom(ret) && paramTypes.length == 0) {
		// Handler action = tag.handler(name);
		// if (action == null) {
		// action = newAction(tag, name, ret);
		// tag.setHandler(name, action);
		// }
		//
		// return action;
		// }

		boolean returnsTag = ret.isAssignableFrom(clazz);

		// attr value setter
		if (name.equals("attr") && returnsTag && paramTypes.length == 2 && paramTypes[0].equals(String.class)
				&& paramTypes[1].equals(String.class)) {
			tag.set((String) args[0], args[1]);
			return target;
		}

		// classs setter
		if (name.equals("classs") && returnsTag && paramTypes.length == 1 && paramTypes[0].equals(String.class)) {
			tag.set("class", args[0]);
			return target;
		}

		// set content
		if (name.equals("content") && paramTypes.length == 1 && paramTypes[0].equals(Object[].class)) {
			tag.content((Object[]) args[0]);
			return target;
		}

		// get content
		if (name.equals("content") && paramTypes.length == 0) {
			return tag.contents;
		}

		// prepend
		if (name.equals("prepend") && paramTypes.length == 1 && paramTypes[0].equals(Object[].class)) {
			tag.prepend((Object[]) args[0]);
			return target;
		}

		// append
		if (name.equals("append") && paramTypes.length == 1 && paramTypes[0].equals(Object[].class)) {
			tag.append((Object[]) args[0]);
			return target;
		}

		if (name.startsWith("on") && name.length() > 2) {

			String event = name.substring(2).toLowerCase();

			// handlers setter
			if (returnsTag && paramTypes.length == 1 && TagEventHandler.class.isAssignableFrom(paramTypes[0])) {
				tag.setHandler(event, (TagEventHandler<TAG>) args[0]);
				return target;
			}

			// action setter
			if (returnsTag && paramTypes.length == 1 && Action[].class.isAssignableFrom(paramTypes[0])) {
				tag.setHandler(event, (Action[]) args[0]);
				return target;
			}
		}

		// value setter
		if (returnsTag && paramTypes.length == 1) {
			tag.set(name, args[0]);
			return target;
		}

		// boolean setter
		if (returnsTag && paramTypes.length == 0) {
			tag.set(name, true);
			return target;
		}

		throw U.rte("Not implemented: " + name);
	}

	// protected Handler newAction(TagData tag, String name, Class<?> clazz) {
	// final ActData prop = new ActData(tag, name);
	//
	// return Cls.implement(prop, new InvocationHandler() {
	//
	// @Override
	// public Object invoke(Object target, Method method, Object[] args) throws Throwable {
	//
	// throw U.notExpected();
	// }
	//
	// }, clazz);
	// }

}
