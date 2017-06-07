package org.rapidoid.gui;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.group.Manageable;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class GUIActions extends GUI {

	public static Object of(Object target) {
		MultiWidget actions = multi();

		if (target instanceof Manageable) {
			Manageable manageable = (Manageable) target;
			for (String action : manageable.getManageableActions()) {
				actions.add(action(manageable, action));
			}
		}

		return actions;
	}

	private static Btn action(final Manageable manageable, final String action) {
		Btn btn = cmd(action, manageable.getClass().getSimpleName(), manageable.group().kind(), manageable.id()).smallest();

		final String cmd = btn.command();
		btn.onClick(new Runnable() {
			@Override
			public void run() {
				manageable.runManageableAction(cmd);
			}
		});

		return btn;
	}
}
