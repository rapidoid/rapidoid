/*-
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.web;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.RapidoidInfo;
import org.rapidoid.env.Env;
import org.rapidoid.u.U;

import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ScreenBean extends RapidoidThing implements Screen {

	private volatile String home;
	private volatile Object brand;
	private volatile String title;
	private volatile Object[] content;
	private volatile Map<String, Object> menu;
	private volatile boolean embedded;
	private volatile boolean search;
	private volatile boolean navbar;
	private volatile boolean fluid;
	private volatile boolean cdn;

	private final Set<String> js = Coll.synchronizedSet();
	private final Set<String> css = Coll.synchronizedSet();

	public ScreenBean() {
		reset();
	}

	@Override
	public void reset() {
		home = "/";
		brand = "Rapidoid";
		title = null;
		content = null;
		menu = null;
		embedded = false;
		search = false;
		navbar = true;
		fluid = false;
		cdn = Env.production() && !RapidoidInfo.isSnapshot();

		js.clear();
		css.clear();
	}

	@Override
	public String render() {
		throw U.rte("The Screen cannot render itself!");
	}

	@Override
	public void render(OutputStream out) {
		throw U.rte("The Screen cannot render itself!");
	}

	@Override
	public String home() {
		return home;
	}

	@Override
	public Screen home(String home) {
		this.home = home;
		return this;
	}

	@Override
	public Object brand() {
		return brand;
	}

	@Override
	public Screen brand(Object brand) {
		this.brand = brand;
		return this;
	}

	@Override
	public String title() {
		return title;
	}

	@Override
	public Screen title(String title) {
		this.title = title;
		return this;
	}

	@Override
	public Object[] content() {
		return content;
	}

	@Override
	public Screen content(Object... content) {
		this.content = content;
		return this;
	}

	@Override
	public Map<String, Object> menu() {
		return menu;
	}

	@Override
	public Screen menu(Map<String, ?> menu) {
		this.menu = U.cast(menu);
		return this;
	}

	@Override
	public boolean embedded() {
		return embedded;
	}

	@Override
	public Screen embedded(boolean embedded) {
		this.embedded = embedded;
		return this;
	}

	@Override
	public boolean search() {
		return search;
	}

	@Override
	public Screen search(boolean search) {
		this.search = search;
		return this;
	}

	@Override
	public boolean navbar() {
		return navbar;
	}

	@Override
	public Screen navbar(boolean navbar) {
		this.navbar = navbar;
		return this;
	}

	@Override
	public boolean fluid() {
		return fluid;
	}

	@Override
	public Screen fluid(boolean fluid) {
		this.fluid = fluid;
		return this;
	}

	@Override
	public boolean cdn() {
		return cdn;
	}

	@Override
	public Screen cdn(boolean cdn) {
		this.cdn = cdn;
		return this;
	}

	@Override
	public Set<String> js() {
		return js;
	}

	@Override
	public Set<String> css() {
		return css;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized Screen addMenuItem(String targetUrl, String... nav) {
		U.must(U.notEmpty(targetUrl), "The target URL cannot be empty!");
		U.must(U.notEmpty(nav), "The menu navigation elements cannot be empty!");

		if (menu == null) {
			menu = Coll.synchronizedMap();
		}

		Map<String, Object> subMenu = menu;
		for (int i = 0; i < nav.length - 1; i++) {
			subMenu = (Map<String, Object>) subMenu.computeIfAbsent(nav[i], x -> Coll.synchronizedMap());
		}

		subMenu.put(nav[nav.length - 1], targetUrl);

		return this;
	}

	@Override
	public void assign(Screen src) {
		home(src.home());
		brand(src.brand());
		title(src.title());
		content(src.content());
		menu(Coll.deepCopyOf(src.menu()));
		embedded(src.embedded());
		search(src.search());
		navbar(src.navbar());
		fluid(src.fluid());
		cdn(src.cdn());

		Coll.assign(js, src.js());
		Coll.assign(css, src.css());
	}

}
