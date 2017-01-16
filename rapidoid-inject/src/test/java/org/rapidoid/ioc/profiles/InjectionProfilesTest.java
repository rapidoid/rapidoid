package org.rapidoid.ioc.profiles;

/*
 * #%L
 * rapidoid-inject
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
import org.rapidoid.env.Env;
import org.rapidoid.ioc.AbstractInjectTest;
import org.rapidoid.ioc.IoC;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class InjectionProfilesTest extends AbstractInjectTest {

	@Test
	public void shouldFailOnNonMatchingProfile() throws Exception {
		withoutABCProfiles();
		IoC.manage(BazA.class, BazB.class, BazC.class);

		noIocBean(BazA.class);
		noIocBean(BazB.class);
		noIocBean(BazC.class);

		verifyIoC();
	}

	@Test
	public void shouldInjectInProfileAAA() {
		addProfile("aaa");
		IoC.manage(BazA.class, BazB.class, BazC.class);

		BazWrapper baz = IoC.singleton(BazWrapper.class);
		noIocBean(BazB.class);
		noIocBean(BazC.class);

		eq(baz.str(), "A");

		verifyIoC();
	}

	@Test
	public void shouldInjectInProfileBBB() {
		addProfile("bbb");
		IoC.manage(BazA.class, BazB.class, BazC.class);

		BazWrapper baz = IoC.singleton(BazWrapper.class);
		noIocBean(BazA.class);
		noIocBean(BazC.class);

		eq(baz.str(), "B");

		verifyIoC();
	}

	@Test
	public void shouldInjectInProfileCCC() {
		addProfile("ccc");
		IoC.manage(BazA.class, BazB.class, BazC.class);

		BazWrapper baz = IoC.singleton(BazWrapper.class);
		noIocBean(BazA.class);
		noIocBean(BazB.class);

		eq(baz.str(), "C");

		verifyIoC();
	}

	private void addProfile(String profile) {
		withoutABCProfiles();
		Env.setProfiles(profile);
		isTrue(Env.hasProfile(profile));
	}

	private void withoutABCProfiles() {
		isFalse(Env.hasProfile("aaa"));
		isFalse(Env.hasProfile("bbb"));
		isFalse(Env.hasProfile("ccc"));
	}

}
