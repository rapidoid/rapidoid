package org.rapidoid.http.processor;

/*
 * #%L
 * rapidoid-http-fast
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
import org.rapidoid.http.FastHttpProtocol;
import org.rapidoid.http.HttpMetadata;
import org.rapidoid.net.Server;
import org.rapidoid.net.TCP;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public abstract class AbstractHttpProcessor extends RapidoidThing implements HttpProcessor, HttpMetadata {

	protected final HttpProcessor next;

	protected volatile boolean syncBufs = true;

	protected AbstractHttpProcessor(HttpProcessor next) {
		this.next = next;
	}

	@Override
	public Server listen(String address, int port) {
		FastHttpProtocol protocol = new FastHttpProtocol(this);
		return TCP.server().protocol(protocol).address(address).port(port).syncBufs(syncBufs).build().start();
	}

	@Override
	public Server listen(int port) {
		return listen("0.0.0.0", port);
	}

	public boolean syncBufs() {
		return syncBufs;
	}

	public void syncBufs(boolean syncBufs) {
		this.syncBufs = syncBufs;
	}

	@Override
	public void waitToInitialize() {
		if (next != null) {
			next.waitToInitialize();
		}
	}
}
