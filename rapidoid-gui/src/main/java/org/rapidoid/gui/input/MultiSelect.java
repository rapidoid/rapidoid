package org.rapidoid.gui.input;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.base.AbstractOptions;
import org.rapidoid.html.tag.OptionTag;
import org.rapidoid.html.tag.SelectTag;
import org.rapidoid.u.U;

import java.util.Collection;

/*
 * #%L
 * rapidoid-gui
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
public class MultiSelect extends AbstractOptions<MultiSelect> {

	protected Collection<?> initial;

	@Override
	protected Object render() {
		U.notNull(options, "options");
		SelectTag select = GUI.select().name(_name()).class_("form-control select2").multiple(true);

		for (Object opt : options) {
			boolean initial = this.initial != null && this.initial.contains(opt);
			OptionTag op = GUI.option(opt).value(str(opt)).selected(picked(opt, initial));
			select = select.append(op);
		}

		return select;
	}

	public Collection<?> initial() {
		return initial;
	}

	public MultiSelect initial(Collection<?> initial) {
		this.initial = initial;
		return this;
	}

}
