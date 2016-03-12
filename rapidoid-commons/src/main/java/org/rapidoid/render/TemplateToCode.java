package org.rapidoid.render;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;
import org.rapidoid.commons.Str;
import org.rapidoid.u.U;

import java.util.concurrent.atomic.AtomicInteger;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class TemplateToCode {

	public static final AtomicInteger ID_GEN = new AtomicInteger();

	public static final String Q = "\"";

	public static String generate(XNode x) {
		String body;

		switch (x.op) {
			case OP_ROOT:
				return "{" + U.join("", x.children) + "}";

			case OP_TEXT:
				return print(literal(x.text));

			case OP_PRINT:
				return val(x.text, true);

			case OP_PRINT_RAW:
				return val(x.text, false);

			case OP_IF_NOT:
				body = U.join("", x.children);
				return U.frmt("if (!$1.cond(%s)) { %s }", literal(x.text), body);

			case OP_IF:
				body = U.join("", x.children);
				return U.frmt("if ($1.cond(%s)) { %s }", literal(x.text), body);

			case OP_INCLUDE:
				return U.frmt("$1.call(%s);", literal(x.text));

			case OP_FOREACH:
				body = U.join("", x.children);
				String arr = "v" + ID_GEN.incrementAndGet();
				String i = "v" + ID_GEN.incrementAndGet();
				String code = "Object[] %s = $1.iter(\"%s\"); for (int %s = 0; %s < %s.length; %s++) {\n %s\n }";
				return U.frmt(code, arr, x.text, i, i, arr, i, scoped(i, arr, body));

			default:
				throw Err.notExpected();
		}
	}

	static String scoped(String i, String arr, String code) {
		String var = arr + "[" + i + "]";
		return U.frmt("$1.push(%s, %s); try { %s } finally { $1.pop(%s, %s); }", i, var, code, i, var);
	}

	static String print(String s) {
		return U.frmt("$1.print(%s);\n", s);
	}

	static String val(String s, boolean escape) {
		String[] parts = s.split("\\|\\|", 2);
		if (parts.length == 2) {
			return U.frmt("$1.valOr(%s, %s, %s);\n", literal(parts[0]), literal(parts[1]), escape);
		} else {
			return U.frmt("$1.val(%s, %s);\n", literal(s), escape);
		}
	}

	static String literal(String s) {
		return Q + Str.javaEscape(s) + Q;
	}

}
