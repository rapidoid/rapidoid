package org.rapidoid.goodies;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.English;
import org.rapidoid.datamodel.IRange;
import org.rapidoid.datamodel.Range;
import org.rapidoid.commons.Str;
import org.rapidoid.gui.Btn;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.Grid;
import org.rapidoid.gui.Pager;
import org.rapidoid.gui.input.Form;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.jpa.JPA;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.u.U;

import javax.persistence.metamodel.EntityType;
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
@Since("5.1.0")
public class X extends RapidoidThing {

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
				return JPA.jpql(jpql).bind(namedArgs).bind(args).all();
			}
		};
	}

	public static ReqRespHandler index(final Class<?> entityType) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				return JPA.of(entityType).all();
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

	public static ReqRespHandler manage(final Class<?> entityType, final String baseUri) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				if (resp.screen().title() == null) {
					resp.screen().title("Manage " + English.plural(name(entityType)));
				}

				long count = JPA.count(entityType);
				int pageSize = 10;
				int pages = (int) Math.ceil(count / (double) pageSize);
				int page = U.or(Cls.convert(req.params().get("page"), Integer.class), 1);

				IRange range = Range.of((page - 1) * pageSize, pageSize);
				List<?> records = JPA.of(entityType).page(range.start(), range.length());

				Grid grid = GUI.grid(records);

				Btn add = GUI.btn("Add " + name(entityType)).primary().go(baseUri + "/add");

				Pager pager = GUI.pager("page").min(1).max(pages).right(true);

				return GUI.multi(grid, GUI.div(pager, add));
			}
		};
	}

	public static ReqRespHandler add(final Class<?> entityType, final String baseUri) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				final Object entity = Cls.newInstance(entityType);

				if (resp.screen().title() == null) {
					resp.screen().title("Add " + name(entityType));
				}

				Btn save = btnSave(entity).go(baseUri + "/manage");
				Btn cancel = GUI.btn("Cancel").go(baseUri + "/manage");

				return GUI.create(entity).buttons(save, cancel);
			}
		};
	}

	public static ReqRespHandler view(final Class<?> entityType, final String baseUri) {
		final Class<?> idType = idType(entityType);

		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, final Resp resp) throws Exception {

				final Object id = Cls.convert(req.param("id"), idType);
				final Object entity = JPA.getIfExists(entityType, id);

				if (entity == null) {
					return null;
				}

				JPA.detach(entity);

				String name = name(entityType);

				if (resp.screen().title() == null) {
					resp.screen().title(name + " Details");
				}

				final Btn edit = GUI.btn("Edit").go(uri(baseUri, entity) + "/edit");

				Btn all = GUI.btn("View all").go(baseUri + "/manage");

				Btn del = btnDelete().go(baseUri + "/manage").onClick(new Runnable() {
					@Override
					public void run() {
						JPA.delete(entityType, id);
					}
				}).confirm("Do you really want to delete the " + name + "?");

				Form form = GUI.show(entity).buttons(edit, all, del).visible(!del.clicked());

				Object msg = del.clicked() ? GUI.div("The data was deleted.") : "";

				return GUI.multi(form, msg);
			}
		};
	}

	public static ReqRespHandler edit(final Class<?> entityType, final String baseUri) {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {

				Object id = Cls.convert(req.param("id"), idType(entityType));
				Object entity = JPA.getIfExists(entityType, id);

				if (entity == null) {
					return null;
				}

				JPA.detach(entity);

				if (resp.screen().title() == null) {
					resp.screen().title("Edit " + name(entityType));
				}

				Btn save = btnSave(entity).go(baseUri + "/manage");
				Btn cancel = GUI.btn("Cancel").go(uri(baseUri, entity) + "/view");

				return GUI.edit(entity).buttons(save, cancel);
			}
		};
	}

	public static Btn btnSave(final Object entity) {
		return GUI.btn("Save").primary().onSuccess(new Runnable() {
			@Override
			public void run() {
				JPA.save(entity);
			}
		});
	}

	public static Btn btnDelete() {
		return GUI.btn("Delete").danger().confirm("Do you really want to delete the data?");
	}

	private static String name(Class<?> entityType) {
		return entityType.getSimpleName();
	}

	private static Class<?> idType(Class<?> entityType) {
		for (EntityType<?> t : JPA.getEntityTypes()) {
			if (t.getJavaType().equals(entityType)) {
				return t.getIdType() != null ? t.getIdType().getJavaType() : null;
			}
		}

		return null;
	}

	public static String uri(String baseUri, Object entity) {
		return GUI.uriFor(baseUri, entity);
	}

	public static void scaffold(String uri, Class<?> entityType) {
		scaffold(On.setup(), uri, entityType);
	}

	public static void scaffold(Setup setup, String uri, Class<?> entityType) {
		JPA.bootstrap(App.path(), entityType);

		scaffoldEntity(setup, uri, entityType);
	}

	public static void scaffold(Class<?>... entityTypes) {
		scaffold(On.setup(), entityTypes);
	}

	public static void scaffold(Setup setup, Class<?>... entityTypes) {
		JPA.bootstrap(App.path(), entityTypes);

		for (Class<?> entityType : entityTypes) {
			scaffoldEntity(setup, GUI.typeUri(entityType), entityType);
		}
	}

	private static void scaffoldEntity(Setup setup, String baseUri, Class<?> entityType) {
		if (baseUri.length() > 1) {
			baseUri = Str.trimr(baseUri, "/");
		}

		// RESTful services
		setup.get(baseUri).json(X.index(entityType));
		setup.get(baseUri + "/{id}").json(X.read(entityType));

		setup.post(baseUri).transaction().json(X.insert(entityType));
		setup.put(baseUri + "/{id}").transaction().json(X.update(entityType));
		setup.delete(baseUri + "/{id}").transaction().json(X.delete(entityType));

		// GUI
		setup.page(baseUri + "/manage").mvc(X.manage(entityType, baseUri));
		setup.page(baseUri + "/add").transaction().mvc(X.add(entityType, baseUri));
		setup.page(baseUri + "/{id}/view").transaction().mvc(X.view(entityType, baseUri));
		setup.page(baseUri + "/{id}/edit").transaction().mvc(X.edit(entityType, baseUri));
	}

}
