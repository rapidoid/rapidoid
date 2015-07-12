package org.rapidoid.net.impl;

/*
 * #%L
 * rapidoid-net
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Inject;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.net.Protocol;
import org.rapidoid.util.U;
import org.rapidoid.wire.Wire;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class RapidoidWorkerThread extends Thread {

	private final int workerIndex;

	private final Protocol protocol;

	private final Class<? extends DefaultExchange<?>> exchangeClass;

	private final Class<? extends RapidoidHelper> helperClass;

	private volatile RapidoidWorker worker;

	@Inject(optional = true)
	private int bufSizeKB = 16;

	@Inject(optional = true)
	private boolean noDelay = false;

	public RapidoidWorkerThread(int workerIndex, Protocol protocol, Class<? extends DefaultExchange<?>> exchangeClass,
			Class<? extends RapidoidHelper> helperClass) {
		super("server" + (workerIndex + 1));
		this.workerIndex = workerIndex;
		this.protocol = protocol;
		this.exchangeClass = exchangeClass;
		this.helperClass = helperClass;
	}

	@Override
	public void run() {
		Wire.autowire(this);

		RapidoidHelper helper = Cls.newInstance(helperClass, exchangeClass);
		helper.requestIdGen = workerIndex; // to generate UNIQUE request ID (+= MAX_IO_WORKERS)

		worker = new RapidoidWorker("server" + (workerIndex + 1), protocol, helper, bufSizeKB, noDelay);

		worker.run();
	}

	public RapidoidWorker getWorker() {
		while (worker == null) {
			U.sleep(50);
		}

		return worker;
	}

}
