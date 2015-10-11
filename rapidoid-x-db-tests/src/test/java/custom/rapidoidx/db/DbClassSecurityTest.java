package custom.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.ctx.UserRoles;
import org.rapidoid.security.annotation.CanChange;
import org.rapidoid.security.annotation.CanInsert;
import org.rapidoid.security.annotation.CanRead;
import org.rapidoidx.db.XDB;
import org.rapidoidx.db.XEntity;

@CanInsert({ "ADMIN", "MANAGER" })
@CanRead({ "AUTHOR" })
@SuppressWarnings("serial")
class Foo extends XEntity {
	public String name = "no name";
}

@CanInsert("LOGGED_IN")
@CanRead("ANYBODY")
@CanChange("MANAGER")
@SuppressWarnings("serial")
class Bar extends XEntity {

	@CanRead(UserRoles.MODERATOR)
	@CanChange({})
	public String name = "no name";

	@CanChange(UserRoles.MANAGER)
	public String desc = "desc";
}

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbClassSecurityTest extends DbTestCommons {

	@Test(expected = SecurityException.class)
	public void testSecurityFailure() {

		Foo foo = new Foo();
		XDB.persist(foo);
		XDB.shutdown();
	}

	@Test(expected = SecurityException.class)
	public void testSecurityFailure2() {

		Foo foo = new Foo();
		XDB.as("moderator@debug").persist(foo);
		XDB.shutdown();
	}

	@Test(expected = SecurityException.class)
	public void testSecurityFailure3() {
		Foo foo = new Foo();
		Ctxs.ctx().setUser(new UserInfo("abcde"));
		XDB.persist(foo);
		XDB.shutdown();
	}

	@Test
	public void testSudo() {

		Foo foo = new Foo();
		XDB.sudo().persist(foo);
		XDB.sudo().update(foo);
		XDB.sudo().refresh(foo);
		XDB.sudo().delete(foo);
		XDB.shutdown();
	}

	@Test
	public void testSecurity() {

		final Foo foo = new Foo();
		XDB.as("admin@debug").persist(foo);
		XDB.shutdown();
	}

	@Test
	public void testSecurity2() {
		Ctxs.ctx().setUser(new UserInfo("manager@debug"));

		Foo foo = new Foo();
		XDB.persist(foo);
		XDB.shutdown();
	}

	@Test
	public void testDeleteSecurity() {

		final Foo foo = new Foo();
		XDB.sudo().persist(foo);

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("admin@debug").delete(foo);
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("asdf").delete(foo);
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.delete(foo);
			}
		});

		XDB.shutdown();
	}

	@Test
	public void testUpdateSecurity() {

		final Foo foo = new Foo();
		XDB.sudo().persist(foo);

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("admin@debug").update(foo);
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("asdf").update(foo);
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.update(foo);
			}
		});

		XDB.shutdown();
	}

	@Test
	public void testGetSecurity() {

		final Foo foo = new Foo();
		foo.name = "abc";
		final long id = XDB.sudo().persist(foo);

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("admin@debug").get(id);
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("asdf").get(id);
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.get(id);
			}
		});

		XDB.shutdown();
	}

	@Test
	public void testColumnGrainedReadSecurity() {

		final Bar bar = new Bar();
		bar.name = "abc";
		final long id = XDB.as("asd").persist(bar);

		Bar bar2 = XDB.get(id);
		eq(bar2.name, null);
		eq(bar2.desc, "desc");

		Bar bar3 = new Bar();
		bar3.id(id);
		XDB.refresh(bar3);
		eq(bar3.name, null);
		eq(bar3.desc, "desc");

		String name = XDB.as("moderator@debug").readColumn(id, "name");
		eq(name, "abc");

		String desc = XDB.as("dfg").readColumn(id, "desc");
		eq(desc, "desc");

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("admin@debug").readColumn(id, "name");
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("asdf").readColumn(id, "name");
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.readColumn(id, "name");
			}
		});

		XDB.shutdown();
	}

	@Test
	public void testColumnGrainedUpdateSecurity() {

		final Bar bar = new Bar();
		bar.name = "abc";
		XDB.as("qwerty").persist(bar);

		eq(bar.id(), 1);
		eq(bar.version(), 1);

		bar.name = "new name";
		bar.desc = "new desc";
		XDB.as("manager@debug").update(bar);

		Bar bar2 = XDB.sudo().get(Long.valueOf(bar.id()));

		eq(bar2.name, "abc");
		eq(bar2.desc, "new desc");

		XDB.shutdown();
	}

	@Test
	public void testRefreshSecurity() {

		final Foo foo = new Foo();
		foo.name = "abc";
		XDB.sudo().persist(foo);

		final Foo foo2 = new Foo();
		foo2.id(foo.id());

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("admin@debug").refresh(foo2);
			}
		});

		eq(foo2.name, "no name");

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("asdf").refresh(foo2);
			}
		});

		eq(foo2.name, "no name");

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.refresh(foo2);
			}
		});

		eq(foo2.name, "no name");

		XDB.shutdown();
	}

	@Test
	public void testClearSecurity() {

		final Foo foo = new Foo();
		XDB.sudo().persist(foo);

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("admin@debug").clear();
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("asdf").clear();
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.clear();
			}
		});

		XDB.shutdown();
	}

}
