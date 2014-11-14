package org.rapidoid.pages;

/*
 * #%L
 * rapidoid-pages
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

import org.rapidoid.html.Tag;
import org.rapidoid.html.TagEventHandler;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.html.tag.DivTag;
import org.rapidoid.html.tag.InputTag;
import org.rapidoid.html.tag.SpanTag;
import org.rapidoid.model.Model;
import org.rapidoid.pages.bootstrap.TableWidget;
import org.rapidoid.pages.entity.Person;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;

@SuppressWarnings("serial")
public class PlaygroundWidget extends HtmlWidget {

	public PlaygroundWidget() {
		setContent(html(grid(1), counter(10), adder()));
	}

	public Tag<?> grid(int page) {
		Object[] data = { new Person("nick", 22), new Person("doe", 44) };

		if (data.length > 0) {
			return div(new TableWidget(Model.beanItems(data)));
		} else {
			return div(_("No results!"));
		}
	}

	public SpanTag counter(int start) {

		final Var<Integer> num = var(start);

		ButtonTag b1 = button("+").click(new TagEventHandler<ButtonTag>() {
			@Override
			public void handle(ButtonTag target) {
				num.set(num.get() + 1);
			}
		});

		ButtonTag b2 = button("-").click(new TagEventHandler<ButtonTag>() {
			@Override
			public void handle(ButtonTag target) {
				num.set(num.get() + 1);
			}
		});

		return span(b2, span(num), b1);
	}

	public DivTag adder() {

		final InputTag input = input().css("border: 1px;");
		final DivTag coll = div();

		ButtonTag b2 = button("+").click(new TagEventHandler<ButtonTag>() {
			@Override
			public void handle(ButtonTag target) {
				U.debug("click", "button", target);
				coll.append(p("added ", input.value()));
			}
		});

		Var<Integer> counter = var(1);

		b2.click(Do.inc(counter, 2), Do.dec(counter, 1));

		return div(span(input, b2), coll);
	}

}
