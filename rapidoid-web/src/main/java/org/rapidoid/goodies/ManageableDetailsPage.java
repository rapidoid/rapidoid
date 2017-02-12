package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.group.GroupOf;
import org.rapidoid.group.Groups;
import org.rapidoid.group.Manageable;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.u.U;

import java.util.List;

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

	@Override
	public Object execute(Req req, Resp resp) {

		String type = req.data("type");
		String id = req.data("id");

		Manageable target = Groups.findMember(type, id);
		U.must(target != null, "Cannot find the manageable!");

		List<String> columns = target.getManageableProperties();

		if (U.notEmpty(columns)) {
			return multi(info(target, columns));

		} else {
			return N_A;
		}
	}

	public Object info(Manageable target, List<String> columns) {
		List<Object> info = U.list();

		GroupOf<? extends Manageable> group = target.group();

		String type = target.getManageableType();
		info.add(breadcrumb(type, group.name(), target.id()));

		info.add(show(target, U.arrayOf(String.class, columns)));

		info.add(autoRefresh(1000));
		return info;
	}

}
