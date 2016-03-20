package org.rapidoid.web;

/*
 * #%L
 * rapidoid-commons
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
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.English;
import org.rapidoid.commons.Str;
import org.rapidoid.gui.Btn;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.Grid;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.jpa.JPA;
import org.rapidoid.setup.On;
import org.rapidoid.sql.JDBC;
import org.rapidoid.u.U;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class X {

	private static final Object OK = U.map();

	public static ReqRespHandler sql(final String sql, final Object... args) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				return JDBC.query(sql, args);
			}
		};
	}

	public static ReqRespHandler jpql(final String jpql, final Object... args) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				Map<String, Object> namedArgs = req != null ? req.data() : null;
				return JPA.jpql(jpql, namedArgs, args);
			}
		};
	}

	public static ReqRespHandler index(final Class<?> entityType) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				return JPA.getAll(entityType);
			}
		};
	}

	public static ReqRespHandler read(final Class<?> entityType) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				Object id = Cls.convert(req.param("id"), idType(entityType));
				return JPA.get(entityType, id);
			}
		};
	}

	public static ReqRespHandler insert(final Class<?> entityType) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				return JPA.save(req.data(entityType));
			}
		};
	}

	public static ReqRespHandler update(final Class<?> entityType) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				Object id = Cls.convert(req.param("id"), idType(entityType));
				JPA.get(entityType, id); // make sure it exists
				JPA.merge(req.data(entityType));  // FIXME improve the merge
				return OK;
			}
		};
	}

	public static ReqRespHandler delete(final Class<?> entityType) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				Object id = Cls.convert(req.param("id"), idType(entityType));
				JPA.delete(entityType, id);
				return OK;
			}
		};
	}

	public static ReqRespHandler manage(final Class<?> entityType) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				if (resp.screen().title() == null) {
					resp.screen().title("Manage " + English.plural(name(entityType)));
				}

				Grid grid = GUI.grid(JPA.getAll(entityType));
				Btn add = GUI.btn("Add " + name(entityType)).href(uri(entityType) + "/add");

				return GUI.multi(grid, add);
			}
		};
	}

	public static ReqRespHandler add(final Class<?> entityType) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				final Object entity = Cls.newInstance(entityType);

				if (resp.screen().title() == null) {
					resp.screen().title("Add " + name(entityType));
				}

				Btn save = btnSave(entity);
				Btn cancel = GUI.btn("Cancel").href(uri(entityType) + "/manage");

				return GUI.create(entity).buttons(save, cancel);
			}
		};
	}

	public static ReqRespHandler view(final Class<?> entityType) {
		final Class<?> idType = idType(entityType);

		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, final Resp resp) throws Exception {

				Object id = Cls.convert(req.param("id"), idType);
				final Object entity = JPA.find(entityType, id);

				if (entity == null) {
					return null;
				}

				if (resp.screen().title() == null) {
					resp.screen().title(name(entityType) + " Details");
				}

				Btn edit = GUI.btn("Edit").href(uri(entity) + "/edit");
				Btn all = GUI.btn("View all").href(uri(entityType) + "/manage");

				Btn del = GUI.btn("Delete").danger().onClick(new Runnable() {
					@Override
					public void run() {
						JPA.delete(entity);
						resp.redirect(uri(entityType));
					}
				});

				return GUI.show(entity).buttons(edit, all, del);
			}
		};
	}

	public static ReqRespHandler edit(final Class<?> entityType) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {

				Object id = Cls.convert(req.param("id"), idType(entityType));
				Object entity = JPA.find(entityType, id);

				if (entity == null) {
					return null;
				}

				if (resp.screen().title() == null) {
					resp.screen().title("Edit " + name(entityType));
				}

				Btn cancel = GUI.btn("Cancel").href(uri(entity) + "/view");

				return GUI.edit(entity).buttons(btnSave(entity), cancel);
			}
		};
	}

	public static Btn btnSave(final Object entity) {
		return GUI.btn("Save").primary().onClick(new Runnable() {
			@Override
			public void run() {
				JPA.save(entity);
			}
		});
	}

	private static String name(Class<?> entityType) {
		return entityType.getSimpleName();
	}

	private static Class<?> idType(Class<?> entityType) {
		Prop idProp = Beany.property(entityType, "id", true);
		return idProp.getType();
	}

	public static String uri(Class<?> entityType) {
		return "/" + English.plural(Str.uncapitalized(entityType.getSimpleName()));
	}

	public static String uri(Object entity) {
		return uri(entity.getClass()) + "/" + JPA.getIdentifier(entity);
	}

	public static void scaffold(String uri, Class<?> entityType) {
		JPA.bootstrap(On.path(), entityType);
		uri = Str.trimr(uri, "/");

		// RESTful services
		On.get(uri).json(X.index(entityType));
		On.get(uri + "/{id}").json(X.read(entityType));

		On.post(uri).tx().json(X.insert(entityType));
		On.put(uri + "/{id}").tx().json(X.update(entityType));
		On.delete(uri + "/{id}").tx().json(X.delete(entityType));

		// GUI
		On.page(uri + "/manage").mvc(X.manage(entityType));
		On.page(uri + "/add").tx().mvc(X.add(entityType));
		On.page(uri + "/{id}/view").tx().mvc(X.view(entityType));
		On.page(uri + "/{id}/edit").tx().mvc(X.edit(entityType));
	}

	public static void scaffold(Class<?>... entityTypes) {
		JPA.bootstrap(On.path(), entityTypes);
		for (Class<?> entityType : entityTypes) {
			String uri = "/" + English.plural(Str.uncapitalized(entityType.getSimpleName()));
			scaffold(uri, entityType);
		}
	}

}
