package org.rapidoid.jpa;

/*
 * #%L
 * rapidoid-jpa
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
import org.rapidoid.datamodel.AbstractDataItems;
import org.rapidoid.u.U;

import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class JPQL extends AbstractDataItems implements Results {

	private final String jpql;

	private final Map<String, ?> namedArgs;

	private final Object[] args;

	public JPQL(String jpql, Map<String, ?> namedArgs, Object[] args) {
		this.jpql = jpql;
		this.namedArgs = namedArgs;
		this.args = args;
	}

	public JPQL(String jpql) {
		this(jpql, null, null);
	}

	public JPQL bind(Object... args) {
		return new JPQL(jpql, null, args);
	}

	public JPQL bind(Map<String, ?> args) {
		return new JPQL(jpql, args, null);
	}

	public int execute() {
		Query q = JPA.em().createQuery(jpql);
		JPA.bind(q, namedArgs, args);
		return q.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public <T> T getSingleResult() {
		Query q = JPA.em().createQuery(jpql);
		JPA.bind(q, namedArgs, args);
		return (T) q.getSingleResult();
	}

	public Query query() {
		Query q = JPA.em().createQuery(jpql);
		JPA.bind(q, namedArgs, args);
		return q;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> all() {
		return query().getResultList();
	}

	@Override
	public <T> List<T> page(int start, int length) {
		return JPAUtil.getPage(query(), start, length);
	}

	public String jpql() {
		return jpql;
	}

	public Map<String, Object> namedArgs() {
		return U.cast(namedArgs);
	}

	public Object[] args() {
		return args;
	}
}
