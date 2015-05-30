package org.rapidoid.demo.taskplanner.model;

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

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.ToString;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.annotation.Since;
import org.rapidoid.jpa.JPAEntity;
import org.rapidoid.security.annotation.CanManage;
import org.rapidoid.util.Role;

@Scaffold
@Entity
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
@CanManage({ Role.MODERATOR, Role.OWNER, Role.SHARED_WITH })
public class User extends JPAEntity {

	@ToString
	public String username;

	public String email;

	public String name;

	public Date birthdate;

	@OneToMany(mappedBy = "owner")
	public Set<Task> ownedTasks;

	@OneToMany(mappedBy = "owner")
	public Set<Comment> ownedComments;

	@ManyToMany(mappedBy = "sharedWith")
	public Set<Task> sharedTasks;

}
