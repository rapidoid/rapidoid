package org.rapidoid.widget;

/*
 * #%L
 * rapidoid-gui
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.base.BootstrapWidgets;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.html.tag.InputTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.model.Models;
import org.rapidoid.var.Var;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class PlaygroundWidget extends BootstrapWidgets {

	public static Tag pageContent(HttpExchange x) {
		return div(gridAt(1), counter(10), adder());
	}

	public static Tag gridAt(int page) {
		Object[] data = { new Person("nick", 22), new Person("doe", 44) };

		if (data.length > 0) {
			return div(grid(Models.beanItemsInfer(data), "", 10));
		} else {
			return div(hardcoded("No results!"));
		}
	}

	public static Tag counter(int start) {

		final Var<Integer> num = var("count", start);

		ButtonTag b1 = button("+");

		ButtonTag b2 = button("-");

		return span(b2, span(num), b1);
	}

	public static Tag adder() {

		final InputTag input = input().style("border: 1px;");
		final Tag coll = div();

		ButtonTag b2 = button("+");

		return div(span(input, b2), coll);
	}

}
