package org.rapidoid.pages.bootstrap;

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

public abstract class NavbarBootstrapPage extends BootstrapPage {

	private static final long serialVersionUID = -774757866486863390L;

	@Override
	protected Object pageBody() {
		Object cont = div(pageContent()).class_(containerMaybeFluid(isFluid()));
		return new Object[] { nav(isFluid(), brand(), navbarContent()), cont };
	}

	protected boolean isFluid() {
		return true;
	}

	protected abstract Object pageContent();

	protected abstract Tag<?> brand();

	protected abstract Object[] navbarContent();

}
