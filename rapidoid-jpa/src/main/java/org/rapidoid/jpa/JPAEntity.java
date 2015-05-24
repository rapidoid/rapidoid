package org.rapidoid.jpa;

/*
 * #%L
 * rapidoid-jpa
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.entity.AbstractEntity;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
@MappedSuperclass
public abstract class JPAEntity extends AbstractEntity {

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Long version;

	@Override
	public String id() {
		return id + "";
	}

	@Override
	public String version() {
		return version + "";
	}

	@Override
	public void id(String id) {
		setId(id != null ? Long.valueOf(id) : null);
	}

	@Override
	public void version(String version) {
		setVersion(version != null ? Long.valueOf(version) : null);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public boolean isOwner(String username) {
		return false;
	}

	public boolean isSharedWith(String username) {
		return false;
	}

}
