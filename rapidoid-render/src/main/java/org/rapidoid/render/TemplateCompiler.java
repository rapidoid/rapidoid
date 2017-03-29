package org.rapidoid.render;

import javassist.*;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * #%L
 * rapidoid-render
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class TemplateCompiler extends RapidoidThing {

	private static final AtomicInteger ID_GEN = new AtomicInteger();

	public static TemplateRenderer compile(XNode node, Class<?> modelType) {
		try {
			Map<String, String> expressions = U.map();
			Map<String, String> constants = U.map();

			String source = TemplateToCode.generate(node, expressions, constants, modelType);

			return tryToCompile(source, expressions, constants, modelType);

		} catch (Exception e) {
			throw U.rte(e);
		}
	}

	private static TemplateRenderer tryToCompile(String source, Map<String, String> expressions, Map<String, String> constants, Class<?> modelType) throws NotFoundException, CannotCompileException,
		InstantiationException, IllegalAccessException {

		ClassPool cp = ClassPool.getDefault();
		CtClass sup = cp.get(Object.class.getCanonicalName());
		CtClass cls = cp.makeClass("RapidoidTemplate" + ID_GEN.incrementAndGet(), sup);

		cls.addInterface(cp.get(TemplateRenderer.class.getCanonicalName()));
		cls.addConstructor(CtNewConstructor.defaultConstructor(cls));

		addExpressions(expressions, cls);
		addConstants(constants, cls);

		CtClass[] params = {cp.get(RenderCtx.class.getCanonicalName())};
		CtClass clsVoid = cp.get(void.class.getCanonicalName());
		cls.addMethod(CtNewMethod.make(Modifier.PUBLIC, clsVoid, "render", params, new CtClass[0], source, cls));

		return (TemplateRenderer) cls.toClass().newInstance();
	}

	private static void addExpressions(Map<String, String> expressions, CtClass cls) throws CannotCompileException {
		for (Map.Entry<String, String> expr : expressions.entrySet()) {
			String fld = "private static final org.rapidoid.render.retriever.ValueRetriever %s = org.rapidoid.render.retriever.Retriever.of(%s);";

			String retrieverId = retrieverId(expr.getKey());
			String prop = expr.getValue();

			String field = U.frmt(fld, retrieverId, prop);

			cls.addField(CtField.make(field, cls));
		}
	}

	private static void addConstants(Map<String, String> constants, CtClass cls) throws CannotCompileException {
		for (Map.Entry<String, String> expr : constants.entrySet()) {
			String fld = "private static final %s = (%s);";

			String field = U.frmt(fld, expr.getKey(), expr.getValue());

			cls.addField(CtField.make(field, cls));
		}
	}

	static String retrieverId(String expr) {
		String id = expr.replaceAll("[^A-Za-z0-9_]", "\\$");
		return "_$_" + id + "_$_";
	}

}
