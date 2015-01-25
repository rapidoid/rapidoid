package org.rapidoid.db;

/*
 * #%L
 * rapidoid-db-tests
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

import org.rapidoid.security.annotation.CanChange;
import org.rapidoid.security.annotation.CanInsert;
import org.rapidoid.security.annotation.CanRead;
import org.rapidoid.util.AppCtx;
import org.rapidoid.util.UserInfo;
import org.testng.annotations.Test;

@CanInsert({ "ADMIN", "MANAGER" })
@CanRead({ "AUTHOR" })
interface IFoo extends Entity {
	DbColumn<String> name();
}

@CanInsert("LOGGED_IN")
@CanRead("ANYBODY")
@CanChange("MANAGER")
interface IBar extends Entity {

	@CanRead(MODERATOR)
	@CanChange({})
	DbColumn<String> name();

	@CanChange(MANAGER)
	DbColumn<String> desc();
}

public class DbInterfaceSecurityTest extends DbTestCommons {

	@Test(expectedExceptions = SecurityException.class)
	public void testSecurityFailure() {

		IFoo foo = DB.entity(IFoo.class);
		DB.persist(foo);
		DB.shutdown();
	}

	@Test(expectedExceptions = SecurityException.class)
	public void testSecurityFailure2() {

		IFoo foo = DB.entity(IFoo.class);
		DB.as("moderator@debug").persist(foo);
		DB.shutdown();
	}

	@Test(expectedExceptions = SecurityException.class)
	public void testSecurityFailure3() {

		IFoo foo = DB.entity(IFoo.class);
		AppCtx.setUser(new UserInfo("abcde"));
		DB.persist(foo);
		DB.shutdown();
	}

	@Test
	public void testSudo() {

		IFoo foo = DB.entity(IFoo.class);
		DB.sudo().persist(foo);
		DB.sudo().update(foo);
		DB.sudo().refresh(foo);
		DB.sudo().delete(foo);
		DB.shutdown();
	}

	@Test
	public void testSecurity() {

		final IFoo foo = DB.entity(IFoo.class);
		DB.as("admin@debug").persist(foo);
		DB.shutdown();
	}

	@Test
	public void testSecurity2() {

		IFoo foo = DB.entity(IFoo.class);
		AppCtx.setUser(new UserInfo("manager@debug"));
		DB.persist(foo);
		DB.shutdown();
	}

	@Test
	public void testDeleteSecurity() {

		final IFoo foo = DB.entity(IFoo.class);
		DB.sudo().persist(foo);

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("admin@debug").delete(foo);
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("asdf").delete(foo);
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.delete(foo);
			}
		});

		DB.shutdown();
	}

	@Test
	public void testUpdateSecurity() {

		final IFoo foo = DB.entity(IFoo.class);
		DB.sudo().persist(foo);

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("admin@debug").update(foo);
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("asdf").update(foo);
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.update(foo);
			}
		});

		DB.shutdown();
	}

	@Test
	public void testGetSecurity() {

		final IFoo foo = DB.entity(IFoo.class);
		foo.name().set("abc");
		final long id = DB.sudo().persist(foo);

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("admin@debug").get(id);
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("asdf").get(id);
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.get(id);
			}
		});

		DB.shutdown();
	}

	@Test
	public void testColumnGrainedReadSecurity() {

		final IBar bar = DB.entity(IBar.class);
		bar.name().set("abc");
		bar.desc().set("desc");
		final long id = DB.as("asd").persist(bar);

		IBar bar2 = DB.get(id);
		eq(bar2.name().get(), null);
		eq(bar2.desc().get(), "desc");

		IBar bar3 = DB.entity(IBar.class);
		bar3.id().set(id);
		DB.refresh(bar3);
		eq(bar3.name().get(), null);
		eq(bar3.desc().get(), "desc");

		String name = DB.as("moderator@debug").readColumn(id, "name");
		eq(name, "abc");

		String desc = DB.as("dfg").readColumn(id, "desc");
		eq(desc, "desc");

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("admin@debug").readColumn(id, "name");
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("asdf").readColumn(id, "name");
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.readColumn(id, "name");
			}
		});

		DB.shutdown();
	}

	@Test
	public void testColumnGrainedUpdateSecurity() {

		final IBar bar = DB.entity(IBar.class);
		bar.name().set("abc");
		DB.as("qwerty").persist(bar);

		eq(bar.id().get().longValue(), 1);
		eq(bar.version().get().longValue(), 1);

		bar.name().set("new name");
		bar.desc().set("new desc");
		DB.as("manager@debug").update(bar);

		IBar bar2 = DB.sudo().get(bar.id().get());

		eq(bar2.name().get(), "abc");
		eq(bar2.desc().get(), "new desc");

		DB.shutdown();
	}

	@Test
	public void testRefreshSecurity() {

		final IFoo foo = DB.entity(IFoo.class);
		foo.name().set("abc");
		DB.sudo().persist(foo);

		final IFoo foo2 = DB.entity(IFoo.class);
		foo2.id().set(foo.id().get());
		foo2.name().set("no name");

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("admin@debug").refresh(foo2);
			}
		});

		eq(foo2.name().get(), "no name");

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("asdf").refresh(foo2);
			}
		});

		eq(foo2.name().get(), "no name");

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.refresh(foo2);
			}
		});

		eq(foo2.name().get(), "no name");

		DB.shutdown();
	}

	@Test
	public void testClearSecurity() {

		final IFoo foo = DB.entity(IFoo.class);
		DB.sudo().persist(foo);

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("admin@debug").clear();
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("asdf").clear();
			}
		});

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.clear();
			}
		});

		DB.shutdown();
	}

}
