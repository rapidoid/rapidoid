package org.rapidoid.jpa;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.ctx.Ctxs;

import javax.persistence.EntityManager;
import java.util.List;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class JPA {

	public static EntityManager em() {
		return Ctxs.ctx().persister();
	}

	public static JPAUtil with(EntityManager em) {
		return JPAUtil.with(em);
	}

	public static Object persist(Object record) {
		return with(em()).persist(record);
	}

	public static <E> List<E> getAll() {
		return with(em()).getAll();
	}

	public static void update(Object id, Object entity) {
		with(em()).update(id, entity);
	}

	public static void update(Object record) {
		with(em()).update(record);
	}

	public static <T> T getIfExists(Class<T> clazz, Object id) {
		return with(em()).getIfExists(clazz, id);
	}

	public static void transaction(Runnable action, boolean readonly) {
		with(em()).transaction(action, readonly);
	}

	public static void transaction(Runnable tx, boolean readonly, Callback<Void> callback) {
		with(em()).transaction(tx, readonly, callback);
	}

	public static <E> List<E> getAll(Class<E> clazz, List<String> ids) {
		return with(em()).getAll(clazz, ids);
	}

	public static <T> List<T> getAll(Class<T> clazz) {
		return with(em()).getAll(clazz);
	}

	public static void refresh(Object entity) {
		with(em()).refresh(entity);
	}

	public static <RESULT> RESULT sql(String sql, Object... args) {
		return with(em()).sql(sql, args);
	}

	public static void delete(Object record) {
		with(em()).delete(record);
	}

	public static Object insert(Object entity) {
		return with(em()).insert(entity);
	}

	public static <E> List<E> getAll(Class<E> clazz, int pageNumber, int pageSize) {
		return with(em()).getAll(clazz, pageNumber, pageSize);
	}

	public static <E> void delete(Class<E> clazz, Object id) {
		with(em()).delete(clazz, id);
	}

	public static <E> E get(Class<E> clazz, Object id) {
		return with(em()).get(clazz, id);
	}

}
