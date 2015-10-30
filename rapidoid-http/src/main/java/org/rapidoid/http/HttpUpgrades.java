package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import java.util.concurrent.ConcurrentMap;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.net.Protocol;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("2.?")
public class HttpUpgrades {

	private final ConcurrentMap<String, HttpUpgradeHandler> upgrades = U.concurrentMap();

	private final ConcurrentMap<String, Protocol> protocols = U.concurrentMap();

	public void add(String upgradeName, HttpUpgradeHandler upgradeHandler, Protocol protocol) {
		upgradeName = upgradeName.toLowerCase();
		upgrades.put(upgradeName, upgradeHandler);
		protocols.put(upgradeName, protocol);
	}

	public HttpUpgradeHandler getUpgrade(String upgradeName) {
		upgradeName = upgradeName.toLowerCase();
		return upgrades.get(upgradeName);
	}

	public Protocol getProtocol(String upgradeName) {
		upgradeName = upgradeName.toLowerCase();
		return protocols.get(upgradeName);
	}

}
