package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
import org.rapidoid.security.Secure;
import org.rapidoid.security.annotation.DevMode;
import org.rapidoid.util.U;
import org.rapidoid.widget.FormWidget;

@DevMode
public class DebugUserInfoScreenBuiltIn extends Screen {

	public Object content() {
		Tag caption = titleBox("Debug Mode - User Information");
		if (Secure.isLoggedIn()) {
			Object userDetails = show(Secure.user(), "name", "username", "email");
			FormWidget userRoles = show(U.map("roles", Secure.getUserRoles(Secure.username())));
			return row(caption, userDetails, userRoles);
		} else {
			return row(caption, h4("Not logged in!"));
		}
	}

}
