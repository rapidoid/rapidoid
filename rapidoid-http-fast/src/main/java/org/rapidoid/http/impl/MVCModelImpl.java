package org.rapidoid.http.impl;

/*
 * #%L
 * rapidoid-http-fast
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;
import org.rapidoid.web.Screen;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class MVCModelImpl extends RapidoidThing implements MVCModel {

	private final Req req;

	private final Resp resp;

	private final Map<String, Object> model;

	private final Screen screen;

	private final Object result;

	public MVCModelImpl(Req req, Resp resp, Map<String, Object> model, Screen screen, Object result) {
		this.req = req;
		this.resp = resp;
		this.model = model;
		this.screen = screen;
		this.result = result;
	}

	@Override
	public Req req() {
		return req;
	}

	@Override
	public Resp resp() {
		return resp;
	}

	@Override
	public Map<String, Object> model() {
		return model;
	}

	@Override
	public Screen screen() {
		return screen;
	}

	@Override
	public Object result() {
		return result;
	}

	@Override
	public String toString() {
		return "MVCModel{" +
			"req=" + req +
			", resp=" + resp +
			", model=" + model +
			", screen=" + (screen != null ? screen.getClass().getSimpleName() : null) +
			", result=" + result +
			'}';
	}
}
