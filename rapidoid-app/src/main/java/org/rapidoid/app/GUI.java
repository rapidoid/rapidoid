package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
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
import org.rapidoid.annotation.Local;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class GUI extends AppGUI {

	@Local
	public String modal = null;

	private HttpExchange ctx;

	protected void showModal(String modalName) {
		modal = modalName;
	}

	protected void hideModal() {
		modal = null;
	}

	public void onCloseModal() {
		modal = null;
	}

	public void onCancel(HttpExchange x) {
		if (modal != null) {
			hideModal();
		} else {
			x.goBack(1);
		}
	}

	public void onBack(HttpExchange x) {
		x.goBack(1);
	}

	protected HttpExchange ctx() {
		U.notNull(ctx, "App context is not initialized yet!");
		return ctx;
	}

}
