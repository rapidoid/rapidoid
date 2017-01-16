package org.rapidoid.goodies.discovery;

/*
 * #%L
 * rapidoid-web
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class PeerDiscoveryInfo extends RapidoidThing {

	public final String clientIpAddress;

	public final String realIpAddress;

	public PeerDiscoveryInfo(String clientIpAddress, String realIpAddress) {
		this.clientIpAddress = clientIpAddress;
		this.realIpAddress = realIpAddress;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PeerDiscoveryInfo that = (PeerDiscoveryInfo) o;

		if (!clientIpAddress.equals(that.clientIpAddress)) return false;
		return realIpAddress.equals(that.realIpAddress);
	}

	@Override
	public int hashCode() {
		int result = clientIpAddress.hashCode();
		result = 31 * result + realIpAddress.hashCode();
		return result;
	}
}
