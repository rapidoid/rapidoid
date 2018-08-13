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
	private volatile boolean embedded;
	private volatile boolean search;
	private volatile boolean navbar;
	private volatile boolean fluid;
	private volatile Boolean cdn;

	private final Set<String> js = Coll.synchronizedSet();
	private final Set<String> css = Coll.synchronizedSet();
	private final Map<String, Object> menu = Coll.synchronizedMap();

	public ScreenBean() {
		reset();
	}

	@Override
	public void reset() {
		home = "/";
		brand = "Rapidoid";
		title = null;
		content = null;
		embedded = false;
		search = false;
		navbar = true;
		fluid = false;
		cdn = null;

		js.clear();
		css.clear();
		menu.clear();
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
	public synchronized Screen addMenuItem(String targetUrl, int order, String caption) {
		String key = menuKey(order, caption);

		menu.put(key, targetUrl);

		return this;
	}

	@Override
	public synchronized Screen addSubMenuItem(String targetUrl, int order, String caption, int subItemOrder, String subItemCaption) {
		U.must(U.notEmpty(targetUrl), "The target URL cannot be empty!");

		String key = menuKey(order, caption);
		Map<String, Object> subMenu = U.cast(menu.computeIfAbsent(key, x -> Coll.synchronizedMap()));

		String subKey = menuKey(subItemOrder, subItemCaption);
		subMenu.put(subKey, targetUrl);

		return this;
	}

	private String menuKey(int order, String caption) {
		return order + caption;
	}

	@Override
	public Screen menu(Map<String, ?> menu) {
		Coll.assign(this.menu, U.cast(menu));
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
	public Boolean cdn() {
		return cdn;
	}

	@Override
	public Screen cdn(Boolean cdn) {
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
