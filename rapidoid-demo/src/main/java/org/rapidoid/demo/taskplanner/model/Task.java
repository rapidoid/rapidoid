package org.rapidoid.demo.taskplanner.model;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.ToString;
import org.rapidoid.annotation.Optional;
import org.rapidoid.annotation.Programmatic;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.annotation.Since;
import org.rapidoid.extra.domain.LowHigh3;
import org.rapidoid.jpa.JPAEntity;
import org.rapidoid.security.annotation.CanChange;
import org.rapidoid.security.annotation.CanManage;
import org.rapidoid.util.Role;

/*
 * #%L
 * rapidoid-demo
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

@Scaffold
@Entity
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Task extends JPAEntity {

	@ToString
	@CanChange({ Role.MODERATOR })
	public String title;

	@ToString
	@CanChange({ Role.MODERATOR, Role.OWNER, Role.SHARED_WITH })
	public LowHigh3 priority = LowHigh3.MEDIUM;

	@Optional
	public String description;

	public int rating;

	@Programmatic
	@CanManage({ Role.OWNER })
	@ManyToOne
	public User owner;

	@CanManage({ Role.OWNER })
	@ManyToMany
	public Set<User> sharedWith;

	@Programmatic
	@OneToMany(mappedBy = "task", cascade = { CascadeType.REMOVE })
	public List<Comment> comments;

}
