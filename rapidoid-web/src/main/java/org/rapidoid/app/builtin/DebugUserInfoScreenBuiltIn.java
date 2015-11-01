package org.rapidoid.app.builtin;

/*
 * #%L
 * rapidoid-web
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
import org.rapidoid.app.GUI;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.Roles;
import org.rapidoid.gui.FormWidget;
import org.rapidoid.html.Tag;
import org.rapidoid.security.annotation.DevMode;
import org.rapidoid.u.U;

@DevMode
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DebugUserInfoScreenBuiltIn extends GUI {

	public Object content() {
		Tag caption = titleBox("Debug Mode - User Information");
		Ctx ctx = Ctxs.ctx();
		if (ctx.isLoggedIn()) {
			Object userDetails = show(ctx.user(), "name", "username", "email");
			FormWidget userRoles = show(U.map("roles", Roles.getRolesFor(ctx.username())));
			return row(caption, userDetails, userRoles);
		} else {
			return row(caption, h4("Not logged in!"));
		}
	}

}
