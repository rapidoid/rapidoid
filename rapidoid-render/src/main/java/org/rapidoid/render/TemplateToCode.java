package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;
import org.rapidoid.commons.Str;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
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
public class TemplateToCode extends RapidoidThing {

	public static final AtomicInteger ID_GEN = new AtomicInteger();

	public static final String Q = "\"";

	public static String generate(XNode x, Map<String, String> expressions, Map<String, String> constants, Class<?> modelType) {
		String body;

		switch (x.op) {
			case OP_ROOT:
				return "{" + join("", x.children, expressions, constants, modelType) + "}";

			case OP_TEXT:
				return U.notEmpty(x.text) ? print(literal(x.text), constants) : "";

			case OP_PRINT:
				return val(x.text, true, expressions);

			case OP_PRINT_RAW:
				return val(x.text, false, expressions);

			case OP_IF_NOT:
				body = join("", x.children, expressions, constants, modelType);
				return U.frmt("if (!$1.cond(%s)) { %s }", literal(x.text), body);

			case OP_IF:
				body = join("", x.children, expressions, constants, modelType);
				return U.frmt("if ($1.cond(%s)) { %s }", literal(x.text), body);

			case OP_INCLUDE:
				return U.frmt("$1.call(%s);", literal(x.text));

			case OP_FOREACH:
				body = join("", x.children, expressions, constants, modelType);
				String retrId = expr(expressions, x.text);

				return iterList(body, retrId);

			default:
				throw Err.notExpected();
		}
	}

	private static String iterList(String body, String retrId) {
		String list = "v" + ID_GEN.incrementAndGet();
		String ind = "v" + ID_GEN.incrementAndGet();

		String code = "java.util.List %s = $1.iter(%s);" +
			" for (int %s = 0; %s < %s.size(); %s++) {\n" +
			" %s\n }";

		return U.frmt(code, list, retrId, ind, ind, list, ind, scopedList(ind, list, body));
	}

	private static String iterArr(String body, String retrId) {
		String arr = "v" + ID_GEN.incrementAndGet();
		String ind = "v" + ID_GEN.incrementAndGet();

		String code = "Object[] %s = $1.iter(%s);" +
			" for (int %s = 0; %s < %s.length; %s++) {\n" +
			" %s\n }";

		return U.frmt(code, arr, retrId, ind, ind, arr, ind, scopedArr(ind, arr, body));
	}

	private static String eachIter(String body, String retrId) {
		String it = "v" + ID_GEN.incrementAndGet();
		String var = "v" + ID_GEN.incrementAndGet();
		String ind = "v" + ID_GEN.incrementAndGet();

		String insideBody = scopedIter(ind, var, body);

		String code = "java.util.Iterator %s = $1.iter(%s);\n" // it, retrId
			+ "int %s = 0;\n " // ind
			+ "while (%s.hasNext()) {\n" // it
			+ "Object %s = %s.next();\n" // var, it
			+ " %s++;\n" // ind
			+ "%s\n }";

		return U.frmt(code, it, retrId, ind, it, var, it, ind, insideBody);
	}

	private static String join(String separator, List<XNode> nodes, Map<String, String> expressions, Map<String, String> constants, Class<?> modelType) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < nodes.size(); i++) {
			if (i > 0) {
				sb.append(separator);
			}

			String code = TemplateToCode.generate(nodes.get(i), expressions, constants, modelType);
			sb.append(code);
		}

		return sb.toString();
	}

	static String scopedList(String ind, String list, String code) {
		String var = list + ".get(" + ind + ")";
		return U.frmt("$1.push(%s, %s); try { %s } finally { $1.pop(%s, %s); }", ind, var, code, ind, var);
	}

	static String scopedArr(String i, String arr, String code) {
		String var = arr + "[" + i + "]";
		return U.frmt("$1.push(%s, %s); try { %s } finally { $1.pop(%s, %s); }", i, var, code, i, var);
	}

	static String scopedIter(String ind, String var, String code) {
		return U.frmt("$1.push(%s, %s); try { %s } finally { $1.pop(%s, %s); }", ind, var, code, ind, var);
	}

	static String print(String s, Map<String, String> constants) {
		if (s.isEmpty()) {
			return "";
		}

		if (Msc.isAscii(s)) {
			String constName = "_C_" + ID_GEN.incrementAndGet();
			constants.put("byte[] " + constName, s + ".getBytes()");

			return U.frmt("$1.printAscii(%s);\n", constName);
		} else {
			return U.frmt("$1.printUTF8(%s);\n", s);
		}
	}

	static String val(String s, boolean escape, Map<String, String> expressions) {

		String[] parts = s.split("\\|\\|", 2);

		if (parts.length == 2) {
			String prop = parts[0];
			String orElse = literal(parts[1]);
			String retrId = expr(expressions, prop);

			return U.frmt("$1.valOr(%s, %s, %s);\n", retrId, orElse, escape);

		} else {
			String retrId = expr(expressions, s);

			return U.frmt("$1.val(%s, %s);\n", retrId, escape);
		}
	}

	static String literal(String s) {
		return Q + Str.javaEscape(s) + Q;
	}

	private static String expr(Map<String, String> expressions, String expr) {
		expressions.put(expr, literal(expr));

		return TemplateCompiler.retrieverId(expr);
	}

}
