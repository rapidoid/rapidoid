package org.rapidoid.env;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Err;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;
import org.rapidoid.util.LazyInit;
import org.rapidoid.util.Msc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

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

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class Environment extends RapidoidInitializer {

	private volatile Set<String> profiles;

	private volatile Set<String> profilesView;

	private volatile EnvMode mode;

	private volatile List<String> args;

	private volatile List<String> argsView;

	private final EnvProperties properties = new EnvProperties();

	private final LazyInit<Map<String, Object>> argsAsMap = new LazyInit<>(new Callable<Map<String, Object>>() {
		@Override
		public Map<String, Object> call() throws Exception {
			U.notNull(args, "environment args");
			return Msc.parseArgs(args);
		}
	});

	public Environment() {
		reset();
	}

	public Set<String> profiles() {
		makeSureIsInitialized();
		return profilesView;
	}

	public EnvMode mode() {
		makeSureIsInitialized();
		return mode;
	}

	private void makeSureIsInitialized() {
		if (profiles == null || mode == null) {
			synchronized (this) {
				if (profiles == null || mode == null) {
					initModeAndProfiles();
				}
			}
		}
	}

	public synchronized void reset() {
		profiles = null;
		mode = null;
		argsAsMap.reset();
		setArgs(); // no args
	}

	@SuppressWarnings("ConstantConditions")
	private void initModeAndProfiles() {

		if (U.isEmpty(profiles)) {
			profiles = Coll.synchronizedSet();
			profiles.addAll(retrieveProfiles());
			profilesView = Collections.unmodifiableSet(profiles);
		}

		boolean production = Msc.isPlatform() || Env.hasInitial("mode", "production") || profiles.contains("production");
		boolean test = Env.hasInitial("mode", "test") || profiles.contains("test");
		boolean dev = Env.hasInitial("mode", "dev") || profiles.contains("dev");

		if (!production && !test && !dev) {
			mode = inferMode();
			if (!silent()) Log.info("No production/dev/test mode was configured, inferring mode", "!mode", mode);

		} else {
			boolean onlyOne = (!production || !dev) && (!dev || !test) && (!production || !test);
			U.must(onlyOne, "Only one of the ('production', 'dev', 'test') profiles can be specified!");

			if (production) {
				mode = EnvMode.PRODUCTION;

			} else if (dev) {
				mode = EnvMode.DEV;

			} else if (test) {
				mode = EnvMode.TEST;

			} else {
				throw Err.notExpected();
			}
		}

		String modeProfile = mode.name().toLowerCase();
		if (!silent()) Log.info("Automatically activating mode-specific profile", "!profile", modeProfile);
		profiles.add(modeProfile);

		if (Msc.isPlatform()) {
			profiles.add("platform");
		}

		RapidoidEnv.touch();

		if (!silent()) Log.info("Initialized environment", "!mode", mode, "!profiles", profiles);
	}

	private static boolean silent() {
		return Msc.isSilent();
	}

	private static EnvMode inferMode() {
		if (Msc.isInsideTest()) {
			return EnvMode.TEST;

		} else {
			if (Msc.dockerized()) return EnvMode.PRODUCTION;

			return ClasspathUtil.getClasspathFolders().isEmpty() ? EnvMode.PRODUCTION : EnvMode.DEV;
		}
	}

	private static List<String> retrieveProfiles() {
		List<String> profiles;

		String profilesLst = Env.initial("profiles");

		if (U.notEmpty(profilesLst)) {
			profiles = U.list(profilesLst.split("\\s*\\,\\s*"));
			if (!silent()) Log.info("Configuring active profiles", "!profiles", profiles);
			return profiles;

		} else {
			if (!Msc.isPlatform()) {
				if (!silent()) Log.info("No profiles were specified, activating 'default' profile");
				profiles = U.list("default");
			} else {
				profiles = U.list();
			}
		}

		return profiles;
	}

	public void setArgs(String... args) {
		this.args = Coll.synchronizedList(args);
		this.argsView = Collections.unmodifiableList(this.args);
		this.argsAsMap.reset();
	}

	public List<String> args() {
		return argsView;
	}

	public boolean hasProfile(String profileName) {
		return profiles().contains(profileName);
	}

	public boolean hasAnyProfile(String... profileNames) {
		for (String profileName : profileNames) {
			if (hasProfile(profileName)) {
				return true;
			}
		}
		return false;
	}

	public synchronized void setProfiles(String... activeProfiles) {

		if (U.isEmpty(profiles)) {
			profiles = Coll.synchronizedSet();
			profilesView = Collections.unmodifiableSet(profiles);
		}

		Collections.addAll(this.profiles, activeProfiles);

		this.mode = null;
		initModeAndProfiles();

		if (!silent())
			Log.info("Activating custom profiles", "!activating", activeProfiles, "!resulting profiles", this.profiles, "!resulting mode", this.mode);
	}

	public boolean isInitialized() {
		return mode != null;
	}

	public EnvProperties properties() {
		RapidoidEnv.touch();
		return properties;
	}

	public Map<String, Object> argsAsMap() {
		RapidoidEnv.touch();
		return argsAsMap.get();
	}

}
