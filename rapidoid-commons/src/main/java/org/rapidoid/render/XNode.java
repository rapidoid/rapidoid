package org.rapidoid.render;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class XNode {

	enum OP {
		OP_ROOT('\0'), // the root node
		OP_TEXT(' '), // normal text
		OP_IF('?'), // {{?items}} ... {{/items}}
		OP_IF_NOT('^'), // {{^items}} ... {{/items}}
		OP_FOREACH('#'), // {{#items}} ... {{/items}}
		OP_INCLUDE('>'), // {{>section}}
		OP_PRINT('$'), // ${x}
		OP_PRINT_RAW('@'); // @{x}

		final char code;

		OP(char code) {
			this.code = code;
		}
	}

	final OP op;
	final String text;
	final List<XNode> children = U.list();

	XNode(OP op, String text) {
		this.op = op;
		this.text = text;
	}

	@Override
	public String toString() {
		return TemplateToCode.generate(this);
	}

	public TemplateRenderer compile() {
		return TemplateCompiler.compile(this);
	}

}
