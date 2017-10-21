package org.rapidoid.config;

/*
 * #%L
 * rapidoid-integration-tests
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

import org.junit.Before;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.env.Env;
import org.rapidoid.env.EnvMode;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.u.U;
import org.rapidoid.util.MscOpts;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ConfigurationTest extends IsolatedIntegrationTest {

	private static boolean TLS_ENABLED = MscOpts.isTLSEnabled();

	@Before
	public void reset() {
		Env.reset();
		Conf.reset();
		Conf.ROOT.setPath("some-non-existing");
	}

	@Test
	public void testBasicConfig() {
		isTrue(Env.test());

		Conf.ROOT.set("abc", "123");
		Conf.ROOT.set("cool", true);

		eq(Conf.ROOT.entry("abc").or(0).longValue(), 123);
		isTrue(Conf.ROOT.is("cool"));

		eq(Env.mode(), EnvMode.TEST);
		isTrue(Env.test());
		isFalse(Env.production());
		isFalse(Env.dev());

		checkDefaults();
	}

	@Test
	public void testDefaultConfig() {
		isTrue(Env.test());

		checkDefaults();
	}

	private void checkDefaults() {
		eq(Conf.ON.entry("port").or(0).longValue(), 8080);
		eq(Conf.ON.entry("address").str().getOrNull(), "127.0.0.1");
	}

	@Test
	public void testProfiles() {
		Env.setArgs("port=12345", "profiles=mysql,p1,p2");

		eq(Env.profiles(), U.set("mysql", "p1", "p2", "test"));

		isTrue(Env.test());
		isFalse(Env.dev());
		isFalse(Env.production());

		checkDefaults();
	}

	@Test
	public void testDefaultProfiles() {
		eq(Env.profiles(), U.set("test", "default"));
	}

	@Test
	public void testPathChange() {
		Env.setArgs("config=myconfig");

		checkDefaults();
	}

	@Test
	public void testUsersConfigWithArgs() {
		String pswd = "m-i_h?1f~@121";
		Env.setArgs("users.admin.password=abc123", "users.nick.password=" + pswd, "users.nick.roles=moderator");

		checkDefaults();

		eq(Conf.USERS.toMap().keySet(), U.set("admin", "nick"));

		eq(Conf.USERS.sub("admin").toMap(), U.map("roles", "administrator", "password", "abc123"));

		eq(Conf.USERS.sub("nick").toMap(), U.map("roles", "moderator", "password", pswd));
	}

	@Test
	public void testEnvironmentProperties() {
		Env.setArgs("config=myconfig");

		String osName = System.getProperty("os.name", "?");

		Config os = Conf.section("os");

		isFalse(os.isEmpty());

		eq(os.get("name"), osName);
		eq(os.entry("name").getOrNull(), osName);
		eq(os.entry("name").or(""), osName);

		isTrue(os.has("name"));
		isFalse(os.is("name"));

		checkDefaults();
	}

	@Test
	public void testArgOverride() {
		Env.setArgs("foo=bar123");

		Conf.ROOT.set("foo", "b");
		eq(Conf.ROOT.get("foo"), "bar123");
	}

	@Test
	public void testNestedSet() {
		Conf.ROOT.set("foo.bar", "b");
		Conf.ROOT.set("foo.baz", "z");

		eq(Conf.section("foo").toMap(), U.map("bar", "b", "baz", "z"));
	}

	@Test
	public void testMySqlProfile() {
		if (TLS_ENABLED) return;

		Env.setArgs("jdbc.port=3333", "profiles=mysql");

		eq(Env.profiles(), U.set("mysql", "test"));

		verifyJson("jdbc-mysql-profile", Conf.JDBC.toMap());
		verifyJson("hibernate-mysql-profile", Conf.HIBERNATE.toMap());
		verifyJson("root", Conf.ROOT.toMap());
	}

	@Test
	public void testPostgresProfile() {
		if (TLS_ENABLED) return;

		Env.setArgs("profiles=postgres");

		eq(Env.profiles(), U.set("postgres", "test"));

		verifyJson("jdbc-postgres-profile", Conf.JDBC.toMap());
		verifyJson("hibernate-postgres-profile", Conf.HIBERNATE.toMap());
		verifyJson("root", Conf.ROOT.toMap());
	}

	@Test
	public void testPlatformProfile() {
		if (TLS_ENABLED) return;

		Env.setArgs("profiles=platform");

		eq(Env.profiles(), U.set("platform", "test"));

		verifyJson("root", Conf.ROOT.toMap());
	}

	@Test
	public void testBuiltInConfig() {
		if (TLS_ENABLED) return;

		verifyJson("root", Conf.ROOT.toMap());
	}

//	private Map<String, Object> rootCfgMasked() {
//		Map<String, Object> root = Conf.ROOT.toMap();
//
//		Map<String, Object> rapidoid = U.cast(root.get("rapidoid"));
//		Map<String, Object> system = U.cast(root.get("system"));
//
//		maskEntries(rapidoid, U.set("snapshot", "builtOn", "version", "nameAndInfo"));
//		maskEntries(system, U.set("cpus"));
//
//		return root;
//	}
//
//	private void maskEntries(Map<String, Object> rapidoid, Set<String> keysToMask) {
//		for (Map.Entry<String, Object> e : rapidoid.entrySet()) {
//			if (keysToMask.contains(e.getKey())) e.setValue("[MASKED]");
//		}
//	}

}
