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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class TemplateToCode extends RapidoidThing {

	public static final AtomicInteger ID_GEN = new AtomicInteger();

	public static final String Q = "\"";

	public static String generate(XNode x, Map<String, String> expressions) {
		String body;

		switch (x.op) {
			case OP_ROOT:
				return "{" + join("", x.children, expressions) + "}";

			case OP_TEXT:
				return print(literal(x.text));

			case OP_PRINT:
				return val(x.text, true, expressions);

			case OP_PRINT_RAW:
				return val(x.text, false, expressions);

			case OP_IF_NOT:
				body = join("", x.children, expressions);
				return U.frmt("if (!$1.cond(%s)) { %s }", literal(x.text), body);

			case OP_IF:
				body = join("", x.children, expressions);
				return U.frmt("if ($1.cond(%s)) { %s }", literal(x.text), body);

			case OP_INCLUDE:
				return U.frmt("$1.call(%s);", literal(x.text));

			case OP_FOREACH:
				body = join("", x.children, expressions);
				String arr = "v" + ID_GEN.incrementAndGet();
				String i = "v" + ID_GEN.incrementAndGet();
				String code = "Object[] %s = $1.iter(\"%s\"); for (int %s = 0; %s < %s.length; %s++) {\n %s\n }";
				return U.frmt(code, arr, x.text, i, i, arr, i, scoped(i, arr, body));

			default:
				throw Err.notExpected();
		}
	}

	private static String join(String separator, List<XNode> nodes, Map<String, String> expressions) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < nodes.size(); i++) {
			if (i > 0) {
				sb.append(separator);
			}

			String code = TemplateToCode.generate(nodes.get(i), expressions);
			sb.append(code);
		}

		return sb.toString();
	}

	static String scoped(String i, String arr, String code) {
		String var = arr + "[" + i + "]";
		return U.frmt("$1.push(%s, %s); try { %s } finally { $1.pop(%s, %s); }", i, var, code, i, var);
	}

	static String print(String s) {
		if (Msc.isAscii(s)) {
			return U.frmt("$1.printAscii(%s);\n", s);
		} else {
			return U.frmt("$1.printUTF8(%s);\n", s);
		}
	}

	static String val(String s, boolean escape, Map<String, String> expressions) {

		String[] parts = s.split("\\|\\|", 2);

		if (parts.length == 2) {
			String prop = parts[0];
			String expr = literal(prop);
			String orElse = literal(parts[1]);
			expressions.put(parts[0], expr);
			return U.frmt("$1.valOr(%s, %s, %s);\n", TemplateCompiler.retrieverId(prop), orElse, escape);

		} else {
			String expr = literal(s);
			expressions.put(s, expr);
			return U.frmt("$1.val(%s, %s);\n", TemplateCompiler.retrieverId(s), escape);
		}
	}

	static String literal(String s) {
		return Q + Str.javaEscape(s) + Q;
	}

}
