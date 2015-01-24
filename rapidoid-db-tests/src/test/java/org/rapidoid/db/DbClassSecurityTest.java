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

import org.rapidoid.security.annotation.CanInsert;
import org.rapidoid.security.annotation.CanRead;
import org.rapidoid.util.AppCtx;
import org.rapidoid.util.UserInfo;
import org.testng.annotations.Test;

@CanInsert({ "ADMIN", "MANAGER" })
@CanRead({ "AUTHOR" })
@SuppressWarnings("serial")
class Foo extends AbstractEntity {
	public String name = "no name";
}

public class DbClassSecurityTest extends DbTestCommons {

	@Test(expectedExceptions = SecurityException.class)
	public void testDbSecurityFailure() {
		AppCtx.reset();
		Foo foo = new Foo();
		DB.persist(foo);
		DB.shutdown();
	}

	@Test(expectedExceptions = SecurityException.class)
	public void testDbSecurityFailure2() {
		AppCtx.reset();
		Foo foo = new Foo();
		DB.as("moderator@debug").persist(foo);
		DB.shutdown();
	}

	@Test(expectedExceptions = SecurityException.class)
	public void testDbSecurityFailure3() {
		AppCtx.reset();
		Foo foo = new Foo();
		AppCtx.setUser(new UserInfo("abcde"));
		DB.persist(foo);
		DB.shutdown();
	}

	@Test
	public void testDbSudo() {
		AppCtx.reset();
		Foo foo = new Foo();
		DB.sudo().persist(foo);
		DB.sudo().update(foo);
		DB.sudo().refresh(foo);
		DB.sudo().delete(foo);
		DB.shutdown();
	}

	@Test
	public void testDbSecurity() {
		AppCtx.reset();
		final Foo foo = new Foo();
		DB.as("admin@debug").persist(foo);
		DB.shutdown();
	}

	@Test
	public void testDbSecurity2() {
		AppCtx.reset();
		Foo foo = new Foo();
		AppCtx.setUser(new UserInfo("manager@debug"));
		DB.persist(foo);
		DB.shutdown();
	}

	@Test
	public void testDbDeleteSecurity() {
		AppCtx.reset();
		final Foo foo = new Foo();
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
	public void testDbUpdateSecurity() {
		AppCtx.reset();
		final Foo foo = new Foo();
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
	public void testDbGetSecurity() {
		AppCtx.reset();
		final Foo foo = new Foo();
		foo.name = "abc";
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
	public void testDbRefreshSecurity() {
		AppCtx.reset();
		final Foo foo = new Foo();
		foo.name = "abc";
		DB.sudo().persist(foo);

		final Foo foo2 = new Foo();
		foo2.id = foo.id;

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("admin@debug").refresh(foo2);
			}
		});

		eq(foo2.name, "no name");

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.as("asdf").refresh(foo2);
			}
		});

		eq(foo2.name, "no name");

		throwsSecurityException(new Runnable() {
			@Override
			public void run() {
				DB.refresh(foo2);
			}
		});

		eq(foo2.name, "no name");

		DB.shutdown();
	}

	@Test
	public void testDbClearSecurity() {
		AppCtx.reset();
		final Foo foo = new Foo();
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
