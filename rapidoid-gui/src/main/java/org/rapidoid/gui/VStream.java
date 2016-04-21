package org.rapidoid.gui;

/*
 * #%L
 * rapidoid-gui
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
import org.rapidoid.commons.Str;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.html.Tag;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.3.0")
public class VStream extends AbstractWidget<VStream> {

	private Object template;

	private String dataUrl;

	private int cols = 1;

	@Override
	protected Tag render() {
		String url = U.or(dataUrl, defaultDataUrl());

		Tag tmpla = div(div(template).attr("ng-if", "it()")).attr("ng-controller", "StreamItemController");
		Tag columns = div(tmpla).class_("col-md-{{12 / cols}}").attr("ng-repeat", "colN in [] | rangex:0:cols");
		Tag tmpl = div(columns).attr("ng-repeat", "rowN in items | rowCount:cols").class_("row row-separated-mini");

		Tag loading = GUI.row("Loading data...").attr("ng-show", "stream.busy");

		Tag scroll = infiniteScroll(tmpl, loading);

		Tag stream = div(scroll).attr("ng-controller", "StreamController").attr("data-url", url)
				.attr("ng-init", "cols = " + cols);
		return stream;
	}

	protected Tag infiniteScroll(Object... contents) {
		Tag scroll = div(contents);

		scroll = scroll.attr("infinite-scroll-disabled", "stream.busy");
		scroll = scroll.attr("infinite-scroll", "stream.nextPage()");
		scroll = scroll.attr("infinite-scroll-distance", "1");

		return scroll;
	}

	protected String defaultDataUrl() {
		String url = Str.trimr(Str.trimr(ReqInfo.get().path(), "/"), ".html");

		if (U.isEmpty(url)) {
			url = "/index";
		}

		return url + ".js";
	}

	public Object template() {
		return template;
	}

	public VStream template(Object template) {
		this.template = template;
		return this;
	}

	public String dataUrl() {
		return dataUrl;
	}

	public VStream dataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
		return this;
	}

	public int cols() {
		return cols;
	}

	public VStream cols(int cols) {
		this.cols = cols;
		return this;
	}

}
