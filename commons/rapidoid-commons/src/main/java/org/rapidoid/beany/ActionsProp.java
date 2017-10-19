package org.rapidoid.beany;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.u.U;
import org.rapidoid.util.MscOpts;

/*
 * #%L
 * rapidoid-commons
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
@Since("5.3.0")
public class ActionsProp extends CustomReadOnlyProp implements Prop {

	public static final String NAME = "(actions)";

	public ActionsProp() {
		U.must(MscOpts.hasRapidoidGUI(), "This special property requires the rapidoid-gui module!");
	}

	public static boolean is(String propName) {
		return propName.equalsIgnoreCase(NAME);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getRaw(Object target) {
		return (T) Cls.invokeStatic("org.rapidoid.gui.GUIActions", "of", target);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
