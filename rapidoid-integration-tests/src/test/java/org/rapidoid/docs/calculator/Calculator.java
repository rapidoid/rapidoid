package org.rapidoid.docs.calculator;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.Page;
import org.rapidoid.gui.Btn;
import org.rapidoid.gui.GUI;
import org.rapidoid.html.Tag;
import org.rapidoid.value.Value;
import org.rapidoid.value.Values;

/*
 * #%L
 * rapidoid-integration-tests
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

@Controller
public class Calculator extends GUI {

	@Page
	public Object calc() {
		Tag btns = div();
		Tag row = div();

		final Value<String> pressed = Values.of("?");

		for (int i = 1; i <= 9; i++) {
			final String digit = "" + i;

			Btn b = btn(digit).command("num", i);

			b.onClick(new Runnable() {
				@Override
				public void run() {
					pressed.set(digit);
				}
			});

			row = row.append(b);
			if (i % 3 == 0) {
				btns = btns.append(row);
				row = div();
			}
		}

		return row(btns, h4("You pressed: ", pressed));
	}

}
