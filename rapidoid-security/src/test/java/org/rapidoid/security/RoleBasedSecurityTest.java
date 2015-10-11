package org.rapidoid.security;

/*
 * #%L
 * rapidoid-security
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import static org.rapidoid.ctx.Roles.LOGGED_IN;
import static org.rapidoid.ctx.Roles.OWNER;
import static org.rapidoid.ctx.Roles.SHARED_WITH;

import java.util.List;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.util.U;

class Foo {
	public String createdBy;
	public List<User> sharedWith;
}

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class RoleBasedSecurityTest extends SecurityTestCommons {

	private void setupRoles() {
		List<String> admin = U.list("adm1", "adm2");
		List<String> manager = U.list("mng1");
		List<String> moderator = U.list("mod1", "mod2");
		List<String> abc = U.list("abc");
		Conf.set("roles", U.map("admin", admin, "manager", manager, "moderator", moderator, "abc", abc));
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

		isFalse(Secure.hasRole(null, LOGGED_IN));
		isFalse(Secure.hasRole("", LOGGED_IN));

		isTrue(Secure.hasRole("abc", LOGGED_IN));
		isTrue(Secure.hasRole("adm1", LOGGED_IN));
		isTrue(Secure.hasRole("mod1", LOGGED_IN));
	}

	@Test
	public void testOwnerRoleCheck() {
		setupRoles();

		isFalse(Secure.hasRole(null, OWNER));
		isFalse(Secure.hasRole("", OWNER));
		isFalse(Secure.hasRole("abc", OWNER));
		isFalse(Secure.hasRole("adm1", OWNER));
		isFalse(Secure.hasRole("mng1", OWNER));
		isFalse(Secure.hasRole("mod1", OWNER));

		isFalse(Secure.hasRoleForClass(null, OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("", OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("abc", OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("adm1", OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("mng1", OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("mod1", OWNER, Foo.class));

		Foo foo = new Foo();

		isFalse(Secure.hasRoleForRecord(null, OWNER, foo));
		isFalse(Secure.hasRoleForRecord("", OWNER, foo));
		isFalse(Secure.hasRoleForRecord("abc", OWNER, foo));
		isFalse(Secure.hasRoleForRecord("adm1", OWNER, foo));
		isFalse(Secure.hasRoleForRecord("mng1", OWNER, foo));
		isFalse(Secure.hasRoleForRecord("mod1", OWNER, foo));

		foo.createdBy = "abc";
		foo.sharedWith = U.list();
		foo.sharedWith.add(new User("adm1"));
		foo.sharedWith.add(new User("mod1"));

		isFalse(Secure.hasRoleForRecord(null, OWNER, foo));
		isFalse(Secure.hasRoleForRecord("", OWNER, foo));
		isFalse(Secure.hasRoleForRecord("adm1", OWNER, foo));
		isFalse(Secure.hasRoleForRecord("mng1", OWNER, foo));
		isFalse(Secure.hasRoleForRecord("mod1", OWNER, foo));

		isTrue(Secure.hasRoleForRecord("abc", OWNER, foo));
	}

	@Test
	public void testSharedWithRoleCheck() {
		setupRoles();

		isFalse(Secure.hasRole(null, SHARED_WITH));
		isFalse(Secure.hasRole("", SHARED_WITH));
		isFalse(Secure.hasRole("abc", SHARED_WITH));
		isFalse(Secure.hasRole("adm1", SHARED_WITH));
		isFalse(Secure.hasRole("mng1", SHARED_WITH));
		isFalse(Secure.hasRole("mod1", SHARED_WITH));

		isFalse(Secure.hasRoleForClass(null, SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("", SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("abc", SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("adm1", SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("mng1", SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("mod1", SHARED_WITH, Foo.class));

		Foo foo = new Foo();

		isFalse(Secure.hasRoleForRecord(null, SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("", SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("abc", SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("adm1", SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("mng1", SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("mod1", SHARED_WITH, foo));

		foo.createdBy = "abc";
		foo.sharedWith = U.list();
		foo.sharedWith.add(new User("adm1"));
		foo.sharedWith.add(new User("mod1"));

		isFalse(Secure.hasRoleForRecord(null, SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("", SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("abc", SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("mng1", SHARED_WITH, foo));

		isTrue(Secure.hasRoleForRecord("adm1", SHARED_WITH, foo));
		isTrue(Secure.hasRoleForRecord("mod1", SHARED_WITH, foo));
	}

}
