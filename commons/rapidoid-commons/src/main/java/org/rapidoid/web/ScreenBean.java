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
@Since("5.1.0")
public class ScreenBean extends RapidoidThing implements Screen {

	private volatile String home = "/";
	private volatile Object brand;
	private volatile String title;
	private volatile Object[] content;
	private volatile Map<String, Object> menu;
	private volatile boolean embedded;
	private volatile boolean search;
	private volatile boolean navbar = true;
	private volatile boolean fluid;
	private volatile boolean cdn = Env.production() && !RapidoidInfo.isSnapshot();

	private final Set<String> js = Coll.synchronizedSet();
	private final Set<String> css = Coll.synchronizedSet();

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

}
