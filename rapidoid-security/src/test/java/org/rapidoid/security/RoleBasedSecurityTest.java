package org.rapidoid.security;

/*
 * #%L
 * rapidoid-security
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.util.List;

import org.rapidoid.security.annotation.Owner;
import org.rapidoid.security.annotation.SharedWith;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

class User {
	public String username;

	public User(String username) {
		this.username = username;
	}
}

class Foo {
	public User owner;
	public List<User> sharedWith;
}

public class RoleBasedSecurityTest extends TestCommons {

	private static final String ROLE_OWNER = Owner.class.getSimpleName().toUpperCase();

	private static final String ROLE_SHARED_WITH = SharedWith.class.getSimpleName().toUpperCase();

	private void setupRoles() {
		U.args("role-admin=adm1,adm2", "role-manager=mng1", "role-moderator=mod1,mod2", "role-abc=abc");
	}

	@Test
	public void testAdminRoleCheck() {
		setupRoles();

		isFalse(Secure.isAdmin(null));
		isFalse(Secure.isAdmin(""));
		isFalse(Secure.isAdmin("abc"));
		isFalse(Secure.isAdmin("mng1"));
		isFalse(Secure.isAdmin("mod1"));
		isFalse(Secure.isAdmin("mod2"));

		isTrue(Secure.isAdmin("adm1"));
		isTrue(Secure.isAdmin("adm2"));
	}

	@Test
	public void testManagerRoleCheck() {
		setupRoles();

		isFalse(Secure.isManager(null));
		isFalse(Secure.isManager(""));
		isFalse(Secure.isManager("abc"));
		isFalse(Secure.isManager("adm1"));
		isFalse(Secure.isManager("adm2"));
		isFalse(Secure.isManager("mod1"));
		isFalse(Secure.isManager("mod2"));

		isTrue(Secure.isManager("mng1"));
	}

	@Test
	public void testModeratorRoleCheck() {
		setupRoles();

		isFalse(Secure.isModerator(null));
		isFalse(Secure.isModerator(""));
		isFalse(Secure.isModerator("abc"));
		isFalse(Secure.isModerator("adm1"));
		isFalse(Secure.isModerator("adm2"));

		isTrue(Secure.isModerator("mod1"));
		isTrue(Secure.isModerator("mod2"));
	}

	@Test
	public void testLoggedInRoleCheck() {
		setupRoles();

		isFalse(Secure.hasRole(null, "LOGGEDIN"));
		isFalse(Secure.hasRole("", "LOGGEDIN"));

		isTrue(Secure.hasRole("abc", "LOGGEDIN"));
		isTrue(Secure.hasRole("adm1", "LOGGEDIN"));
		isTrue(Secure.hasRole("mod1", "LOGGEDIN"));
	}

	@Test
	public void testOwnerRoleCheck() {
		setupRoles();

		isFalse(Secure.hasRole(null, ROLE_OWNER));
		isFalse(Secure.hasRole("", ROLE_OWNER));
		isFalse(Secure.hasRole("abc", ROLE_OWNER));
		isFalse(Secure.hasRole("adm1", ROLE_OWNER));
		isFalse(Secure.hasRole("mng1", ROLE_OWNER));
		isFalse(Secure.hasRole("mod1", ROLE_OWNER));

		isFalse(Secure.hasRoleForClass(null, ROLE_OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("", ROLE_OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("abc", ROLE_OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("adm1", ROLE_OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("mng1", ROLE_OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("mod1", ROLE_OWNER, Foo.class));

		Foo foo = new Foo();

		isFalse(Secure.hasRoleForRecord(null, ROLE_OWNER, foo));
		isFalse(Secure.hasRoleForRecord("", ROLE_OWNER, foo));
		isFalse(Secure.hasRoleForRecord("abc", ROLE_OWNER, foo));
		isFalse(Secure.hasRoleForRecord("adm1", ROLE_OWNER, foo));
		isFalse(Secure.hasRoleForRecord("mng1", ROLE_OWNER, foo));
		isFalse(Secure.hasRoleForRecord("mod1", ROLE_OWNER, foo));

		foo.owner = new User("abc");
		foo.sharedWith = U.list();
		foo.sharedWith.add(new User("adm1"));
		foo.sharedWith.add(new User("mod1"));

		isFalse(Secure.hasRoleForRecord(null, ROLE_OWNER, foo));
		isFalse(Secure.hasRoleForRecord("", ROLE_OWNER, foo));
		isFalse(Secure.hasRoleForRecord("adm1", ROLE_OWNER, foo));
		isFalse(Secure.hasRoleForRecord("mng1", ROLE_OWNER, foo));
		isFalse(Secure.hasRoleForRecord("mod1", ROLE_OWNER, foo));

		isTrue(Secure.hasRoleForRecord("abc", ROLE_OWNER, foo));
	}

	@Test
	public void testSharedWithRoleCheck() {
		setupRoles();

		isFalse(Secure.hasRole(null, ROLE_SHARED_WITH));
		isFalse(Secure.hasRole("", ROLE_SHARED_WITH));
		isFalse(Secure.hasRole("abc", ROLE_SHARED_WITH));
		isFalse(Secure.hasRole("adm1", ROLE_SHARED_WITH));
		isFalse(Secure.hasRole("mng1", ROLE_SHARED_WITH));
		isFalse(Secure.hasRole("mod1", ROLE_SHARED_WITH));

		isFalse(Secure.hasRoleForClass(null, ROLE_SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("", ROLE_SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("abc", ROLE_SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("adm1", ROLE_SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("mng1", ROLE_SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("mod1", ROLE_SHARED_WITH, Foo.class));

		Foo foo = new Foo();

		isFalse(Secure.hasRoleForRecord(null, ROLE_SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("", ROLE_SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("abc", ROLE_SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("adm1", ROLE_SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("mng1", ROLE_SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("mod1", ROLE_SHARED_WITH, foo));

		foo.owner = new User("abc");
		foo.sharedWith = U.list();
		foo.sharedWith.add(new User("adm1"));
		foo.sharedWith.add(new User("mod1"));

		isFalse(Secure.hasRoleForRecord(null, ROLE_SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("", ROLE_SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("abc", ROLE_SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("mng1", ROLE_SHARED_WITH, foo));

		isTrue(Secure.hasRoleForRecord("adm1", ROLE_SHARED_WITH, foo));
		isTrue(Secure.hasRoleForRecord("mod1", ROLE_SHARED_WITH, foo));
	}

}
