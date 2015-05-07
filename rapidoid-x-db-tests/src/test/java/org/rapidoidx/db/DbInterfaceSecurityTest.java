package org.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.entity.IEntity;
import org.rapidoid.security.annotation.CanChange;
import org.rapidoid.security.annotation.CanInsert;
import org.rapidoid.security.annotation.CanRead;
import org.rapidoid.util.AppCtx;
import org.rapidoid.util.CommonRoles;
import org.rapidoid.util.UserInfo;
import org.testng.annotations.Test;

@CanInsert({ "ADMIN", "MANAGER" })
@CanRead({ "AUTHOR" })
interface IFoo extends IEntity {
	DbColumn<String> name();
}

@CanInsert("LOGGED_IN")
@CanRead("ANYBODY")
@CanChange("MANAGER")
interface IBar extends IEntity, CommonRoles {

	@CanRead(MODERATOR)
	@CanChange({})
	DbColumn<String> name();

	@CanChange(MANAGER)
	DbColumn<String> desc();
}

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbInterfaceSecurityTest extends DbTestCommons {

	@Test(expectedExceptions = SecurityException.class)
	public void testSecurityFailure() {

		IFoo foo = XDB.entity(IFoo.class);
		XDB.persist(foo);
		XDB.shutdown();
	}

	@Test(expectedExceptions = SecurityException.class)
	public void testSecurityFailure2() {

		IFoo foo = XDB.entity(IFoo.class);
		XDB.as("moderator@debug").persist(foo);
		XDB.shutdown();
	}

	@Test(expectedExceptions = SecurityException.class)
	public void testSecurityFailure3() {

		IFoo foo = XDB.entity(IFoo.class);
		AppCtx.setUser(new UserInfo("abcde"));
		XDB.persist(foo);
		XDB.shutdown();
	}

	@Test
	public void testSudo() {

		IFoo foo = XDB.entity(IFoo.class);
		XDB.sudo().persist(foo);
		XDB.sudo().update(foo);
		XDB.sudo().refresh(foo);
		XDB.sudo().delete(foo);
		XDB.shutdown();
	}

	@Test
	public void testSecurity() {

		final IFoo foo = XDB.entity(IFoo.class);
		XDB.as("admin@debug").persist(foo);
		XDB.shutdown();
	}

	@Test
	public void testSecurity2() {

		IFoo foo = XDB.entity(IFoo.class);
		AppCtx.setUser(new UserInfo("manager@debug"));
		XDB.persist(foo);
		XDB.shutdown();
	}

	@Test
	public void testDeleteSecurity() {

		final IFoo foo = XDB.entity(IFoo.class);
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

		final IFoo foo = XDB.entity(IFoo.class);
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

		final IFoo foo = XDB.entity(IFoo.class);
		foo.name().set("abc");
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

		final IBar bar = XDB.entity(IBar.class);
		bar.name().set("abc");
		bar.desc().set("desc");
		final long id = XDB.as("asd").persist(bar);

		IBar bar2 = XDB.get(id);
		eq(bar2.name().get(), null);
		eq(bar2.desc().get(), "desc");

		IBar bar3 = XDB.entity(IBar.class);
		bar3.id(id);
		XDB.refresh(bar3);
		eq(bar3.name().get(), null);
		eq(bar3.desc().get(), "desc");

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

		final IBar bar = XDB.entity(IBar.class);
		bar.name().set("abc");
		XDB.as("qwerty").persist(bar);

		eq(bar.id(), 1);
		eq(bar.version(), 1);

		bar.name().set("new name");
		bar.desc().set("new desc");
		XDB.as("manager@debug").update(bar);

		IBar bar2 = XDB.sudo().get(bar.id());

		eq(bar2.name().get(), "abc");
		eq(bar2.desc().get(), "new desc");

		XDB.shutdown();
	}

	@Test
	public void testRefreshSecurity() {

		final IFoo foo = XDB.entity(IFoo.class);
		foo.name().set("abc");
		XDB.sudo().persist(foo);

		final IFoo foo2 = XDB.entity(IFoo.class);
		foo2.id(foo.id());
		foo2.name().set("no name");

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("admin@debug").refresh(foo2);
			}
		});

		eq(foo2.name().get(), "no name");

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.as("asdf").refresh(foo2);
			}
		});

		eq(foo2.name().get(), "no name");

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				XDB.refresh(foo2);
			}
		});

		eq(foo2.name().get(), "no name");

		XDB.shutdown();
	}

	@Test
	public void testClearSecurity() {

		final IFoo foo = XDB.entity(IFoo.class);
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
