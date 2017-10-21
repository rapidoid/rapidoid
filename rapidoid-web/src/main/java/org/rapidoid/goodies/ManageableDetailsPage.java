package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.group.Manageable;
import org.rapidoid.group.Manageables;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * #%L
 * rapidoid-web
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
@Since("5.3.0")
public class ManageableDetailsPage extends GUI implements ReqRespHandler {

	private volatile String mngType;
	private volatile String mngId;
	private volatile String mngSub;

	private volatile String baseUri;

	@Override
	public Object execute(Req req, Resp resp) {

		String type = mngType != null ? mngType : req.<String>data("type");
		String id = mngId != null ? mngId : req.<String>data("id");
		String sub = mngSub != null ? mngSub : req.<String>data("_", null);

		List<String> nav = U.list(type, id);
		if (U.notEmpty(sub)) Collections.addAll(nav, sub.split("/"));

		Manageable target = Manageables.find(type, id, sub);

		Object customDisplay = target.getManageableDisplay();

		return customDisplay != null ? customDisplay : genericDisplay(target, nav);
	}

	private Object genericDisplay(Manageable target, List<String> nav) {
		List<String> columns = target.getManageableProperties();

		if (U.notEmpty(columns)) {
			return multi(info(target, columns, nav));

		} else {
			return N_A;
		}
	}

	private Object info(Manageable target, List<String> columns, List<String> nav) {
		List<Object> info = U.list();

		String kind = target.kind();
		String back = this.baseUri;

		Map<String, String> breadcrumb = U.map(kind, back, target.id(), "#"); // FIXME
		info.add(breadcrumb(breadcrumb)); // .uriPrefix

		info.add(show(target, U.arrayOf(String.class, columns)));

		Map<String, List<Manageable>> children = target.getManageableChildren();

		for (Map.Entry<String, List<Manageable>> e : children.entrySet()) {

			String section = e.getKey();
			info.add(h3(Str.phrase(section) + ":"));

			List<String> snav = U.list(nav);
			snav.add(section);

			ManageablesOverviewPage.addInfo(baseUri, info, snav, e.getValue());
		}

		info.add(autoRefresh(5000));

		return info;
	}

	public String mngType() {
		return mngType;
	}

	public ManageableDetailsPage mngType(String mngType) {
		this.mngType = mngType;
		return this;
	}

	public String mngId() {
		return mngId;
	}

	public ManageableDetailsPage mngId(String mngId) {
		this.mngId = mngId;
		return this;
	}

	public String mngSub() {
		return mngSub;
	}

	public ManageableDetailsPage mngSub(String mngSub) {
		this.mngSub = mngSub;
		return this;
	}

	public String baseUri() {
		return baseUri;
	}

	public ManageableDetailsPage baseUri(String baseUri) {
		this.baseUri = baseUri;
		return this;
	}
}
