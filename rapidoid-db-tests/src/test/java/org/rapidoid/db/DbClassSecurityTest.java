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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.security.annotation.CanChange;
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

@CanInsert("LOGGED_IN")
@CanRead("ANYBODY")
@CanChange("MANAGER")
@SuppressWarnings("serial")
class Bar extends AbstractEntity {

	@CanRead(MODERATOR)
	@CanChange({})
	public String name = "no name";

	@CanChange(MANAGER)
	public String desc = "desc";
}

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DbClassSecurityTest extends DbTestCommons {

	@Test(expectedExceptions = SecurityException.class)
	public void testSecurityFailure() {
		
		Foo foo = new Foo();
		DB.persist(foo);
		DB.shutdown();
	}

	@Test(expectedExceptions = SecurityException.class)
	public void testSecurityFailure2() {
		
		Foo foo = new Foo();
		DB.as("moderator@debug").persist(foo);
		DB.shutdown();
	}

	@Test(expectedExceptions = SecurityException.class)
	public void testSecurityFailure3() {
		
		Foo foo = new Foo();
		AppCtx.setUser(new UserInfo("abcde"));
		DB.persist(foo);
		DB.shutdown();
	}

	@Test
	public void testSudo() {
		
		Foo foo = new Foo();
		DB.sudo().persist(foo);
		DB.sudo().update(foo);
		DB.sudo().refresh(foo);
		DB.sudo().delete(foo);
		DB.shutdown();
	}

	@Test
	public void testSecurity() {
		
		final Foo foo = new Foo();
		DB.as("admin@debug").persist(foo);
		DB.shutdown();
	}

	@Test
	public void testSecurity2() {
		
		Foo foo = new Foo();
		AppCtx.setUser(new UserInfo("manager@debug"));
		DB.persist(foo);
		DB.shutdown();
	}

	@Test
	public void testDeleteSecurity() {
		
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
	public void testUpdateSecurity() {
		
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
	public void testGetSecurity() {
		
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
	public void testColumnGrainedReadSecurity() {
		
		final Bar bar = new Bar();
		bar.name = "abc";
		final long id = DB.as("asd").persist(bar);

		Bar bar2 = DB.get(id);
		eq(bar2.name, null);
		eq(bar2.desc, "desc");

		Bar bar3 = new Bar();
		bar3.id = id;
		DB.refresh(bar3);
		eq(bar3.name, null);
		eq(bar3.desc, "desc");

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
		
		final Bar bar = new Bar();
		bar.name = "abc";
		DB.as("qwerty").persist(bar);

		eq(bar.id, 1);
		eq(bar.version, 1);
		
		bar.name = "new name";
		bar.desc = "new desc";
		DB.as("manager@debug").update(bar);

		Bar bar2 = DB.sudo().get(bar.id);

		eq(bar2.name, "abc");
		eq(bar2.desc, "new desc");

		DB.shutdown();
	}

	@Test
	public void testRefreshSecurity() {
		
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
	public void testClearSecurity() {
		
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
