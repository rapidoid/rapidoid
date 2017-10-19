package org.rapidoid.net.impl;

/*
 * #%L
 * rapidoid-net
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

import org.rapidoid.activity.RapidoidThread;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.net.Protocol;
import org.rapidoid.u.U;

import javax.net.ssl.SSLContext;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class RapidoidWorkerThread extends RapidoidThread {

	private final int workerIndex;

	private final Protocol protocol;

	private final Class<? extends DefaultExchange<?>> exchangeClass;

	private final Class<? extends RapidoidHelper> helperClass;

	private volatile RapidoidWorker worker;

	private final int bufSizeKB;

	private final boolean noDelay;

	private final boolean syncBufs;

	private final SSLContext sslContext;

	public RapidoidWorkerThread(int workerIndex, Protocol protocol, Class<? extends DefaultExchange<?>> exchangeClass,
	                            Class<? extends RapidoidHelper> helperClass, int bufSizeKB, boolean noDelay,
	                            boolean syncBufs, SSLContext sslContext) {

		super("server" + (workerIndex + 1));

		this.workerIndex = workerIndex;
		this.protocol = protocol;
		this.exchangeClass = exchangeClass;
		this.helperClass = helperClass;
		this.bufSizeKB = bufSizeKB;
		this.noDelay = noDelay;
		this.syncBufs = syncBufs;
		this.sslContext = sslContext;
	}

	@Override
	public void run() {
		RapidoidHelper helper = Cls.newInstance(helperClass, exchangeClass);
		helper.requestIdGen = workerIndex; // to generate UNIQUE request ID (+= MAX_IO_WORKERS)

		worker = new RapidoidWorker("server" + (workerIndex + 1), protocol, helper, bufSizeKB, noDelay, syncBufs, sslContext);

		worker.run();
	}

	public RapidoidWorker getWorker() {
		while (worker == null) {
			U.sleep(50);
		}

		return worker;
	}

}
