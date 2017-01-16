package org.rapidoid.security;

/*
 * #%L
 * rapidoid-commons
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.List;

import static org.rapidoid.security.Role.*;

class Foo {
	public String createdBy;
	public List<User> sharedWith;
}

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class RoleBasedSecurityTest extends SecurityTestCommons {

	@Test
	public void testAdminRoleCheck() {
		isFalse(Secure.isAdministrator(null, roles(null)));
		isFalse(Secure.isAdministrator("", roles("")));
		isFalse(Secure.isAdministrator("abc", roles("abc")));
		isFalse(Secure.isAdministrator("mng1", roles("mng1")));
		isFalse(Secure.isAdministrator("mod1", roles("mod1")));
		isFalse(Secure.isAdministrator("mod2", roles("mod2")));

		isTrue(Secure.isAdministrator("adm1", roles("adm1")));
		isTrue(Secure.isAdministrator("adm2", roles("adm2")));
	}

	@Test
	public void testManagerRoleCheck() {
		isFalse(Secure.isManager(null, roles(null)));
		isFalse(Secure.isManager("", roles("")));
		isFalse(Secure.isManager("abc", roles("abc")));
		isFalse(Secure.isManager("adm1", roles("adm1")));
		isFalse(Secure.isManager("adm2", roles("adm2")));
		isFalse(Secure.isManager("mod1", roles("mod1")));
		isFalse(Secure.isManager("mod2", roles("mod2")));

		isTrue(Secure.isManager("mng1", roles("mng1")));
	}

	@Test
	public void testModeratorRoleCheck() {
		isFalse(Secure.isModerator(null, roles(null)));
		isFalse(Secure.isModerator("", roles("")));
		isFalse(Secure.isModerator("abc", roles("abc")));
		isFalse(Secure.isModerator("adm1", roles("adm1")));
		isFalse(Secure.isModerator("adm2", roles("adm2")));

		isTrue(Secure.isModerator("mod1", roles("mod1")));
		isTrue(Secure.isModerator("mod2", roles("mod2")));
	}

	@Test
	public void testLoggedInRoleCheck() {
		isFalse(Secure.hasRole(null, roles(null), LOGGED_IN));
		isFalse(Secure.hasRole("", roles(""), LOGGED_IN));

		isTrue(Secure.hasRole("abc", roles("abc"), LOGGED_IN));
		isTrue(Secure.hasRole("adm1", roles("adm1"), LOGGED_IN));
		isTrue(Secure.hasRole("mod1", roles("mod1"), LOGGED_IN));
	}

	@Test
	public void testOwnerRoleCheck() {
		isFalse(Secure.hasRole(null, roles(null), OWNER));
		isFalse(Secure.hasRole("", roles(""), OWNER));
		isFalse(Secure.hasRole("abc", roles("abc"), OWNER));
		isFalse(Secure.hasRole("adm1", roles("adm1"), OWNER));
		isFalse(Secure.hasRole("mng1", roles("mng1"), OWNER));
		isFalse(Secure.hasRole("mod1", roles("mod1"), OWNER));

		isFalse(Secure.hasRoleForClass(null, roles(null), OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("", roles(""), OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("abc", roles("abc"), OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("adm1", roles("adm1"), OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("mng1", roles("mng1"), OWNER, Foo.class));
		isFalse(Secure.hasRoleForClass("mod1", roles("mod1"), OWNER, Foo.class));

		Foo foo = new Foo();

		isFalse(Secure.hasRoleForRecord(null, roles(null), OWNER, foo));
		isFalse(Secure.hasRoleForRecord("", roles(""), OWNER, foo));
		isFalse(Secure.hasRoleForRecord("abc", roles("abc"), OWNER, foo));
		isFalse(Secure.hasRoleForRecord("adm1", roles("adm1"), OWNER, foo));
		isFalse(Secure.hasRoleForRecord("mng1", roles("mng1"), OWNER, foo));
		isFalse(Secure.hasRoleForRecord("mod1", roles("mod1"), OWNER, foo));

		foo.createdBy = "abc";
		foo.sharedWith = U.list();
		foo.sharedWith.add(new User("adm1"));
		foo.sharedWith.add(new User("mod1"));

		isFalse(Secure.hasRoleForRecord(null, roles(null), OWNER, foo));
		isFalse(Secure.hasRoleForRecord("", roles(""), OWNER, foo));
		isFalse(Secure.hasRoleForRecord("adm1", roles("adm1"), OWNER, foo));
		isFalse(Secure.hasRoleForRecord("mng1", roles("mng1"), OWNER, foo));
		isFalse(Secure.hasRoleForRecord("mod1", roles("mod1"), OWNER, foo));

		isTrue(Secure.hasRoleForRecord("abc", roles("abc"), OWNER, foo));
	}

	@Test
	public void testSharedWithRoleCheck() {
		isFalse(Secure.hasRole(null, roles(null), SHARED_WITH));
		isFalse(Secure.hasRole("", roles(""), SHARED_WITH));
		isFalse(Secure.hasRole("abc", roles("abc"), SHARED_WITH));
		isFalse(Secure.hasRole("adm1", roles("adm1"), SHARED_WITH));
		isFalse(Secure.hasRole("mng1", roles("mng1"), SHARED_WITH));
		isFalse(Secure.hasRole("mod1", roles("mod1"), SHARED_WITH));

		isFalse(Secure.hasRoleForClass(null, roles(null), SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("", roles(""), SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("abc", roles("abc"), SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("adm1", roles("adm1"), SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("mng1", roles("mng1"), SHARED_WITH, Foo.class));
		isFalse(Secure.hasRoleForClass("mod1", roles("mod1"), SHARED_WITH, Foo.class));

		Foo foo = new Foo();

		isFalse(Secure.hasRoleForRecord(null, roles(null), SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("", roles(""), SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("abc", roles("abc"), SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("adm1", roles("adm1"), SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("mng1", roles("mng1"), SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("mod1", roles("mod1"), SHARED_WITH, foo));

		foo.createdBy = "abc";
		foo.sharedWith = U.list();
		foo.sharedWith.add(new User("adm1"));
		foo.sharedWith.add(new User("mod1"));

		isFalse(Secure.hasRoleForRecord(null, roles(null), SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("", roles(""), SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("abc", roles("abc"), SHARED_WITH, foo));
		isFalse(Secure.hasRoleForRecord("mng1", roles("mng1"), SHARED_WITH, foo));

		isTrue(Secure.hasRoleForRecord("adm1", roles("adm1"), SHARED_WITH, foo));
		isTrue(Secure.hasRoleForRecord("mod1", roles("mod1"), SHARED_WITH, foo));
	}

}
