package org.rapidoid.render;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.List;

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
public class XNode extends RapidoidThing {

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

	public TemplateRenderer compile(Class<?> modelType) {
		return TemplateCompiler.compile(this, modelType);
	}

	public TemplateRenderer compile() {
		return compile(Object.class);
	}

}
