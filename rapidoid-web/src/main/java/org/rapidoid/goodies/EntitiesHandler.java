package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.Grid;
import org.rapidoid.jpa.JPA;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;

import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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
@Since("5.1.0")
public class EntitiesHandler extends GUI implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		if (JPA.isActive()) {

			List<Object> data = U.list();

			Grid grid = GUI.grid(recordsInfo()).pageSize(0);

			grid.toUri(new Mapper<Map<String, ?>, String>() {
				@Override
				public String map(Map<String, ?> row) throws Exception {
					return GUI.typeUri(row.get("type") + "") + "/manage";
				}
			});

			data.add(div(h3("Database records:"), grid));

			return multi(data);

		} else {
			return h3("JPA is not enabled in Rapidoid!");
		}
	}

	private List<Map<String, ?>> recordsInfo() {
		List<Map<String, ?>> records = U.list();

		for (EntityType<?> type : JPA.getEntityTypes()) {
			Class<?> javaType = type.getJavaType();

			long count = JPA.count(javaType);
			String idType = type.getIdType() != null ? type.getIdType().getJavaType().getSimpleName() : "";
			Object superType = type.getSupertype() != null ? type.getSupertype().getJavaType().getSimpleName() : "";

			records.add(U.map("type", type.getName(), "extends", superType, "ID Type", idType, "count", count));
		}

		return records;
	}

}
