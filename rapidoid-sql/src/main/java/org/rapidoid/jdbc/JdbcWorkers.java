package org.rapidoid.jdbc;

/*
 * #%L
 * rapidoid-sql
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
import org.rapidoid.config.Conf;
import org.rapidoid.config.ConfigUtil;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.Log;

import java.sql.Connection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;

@Authors("Nikolche Mihajlovski")
@Since("5.3.4")
public class JdbcWorkers extends RapidoidThing {

	private static final int DEFAULT_CAPACITY = 1_000_000;

	private final BlockingQueue<Operation<Connection>> queue;
	private final JdbcWorker[] workers;

	public JdbcWorkers(JdbcClient jdbc) {
		this(jdbc, new ArrayBlockingQueue<Operation<Connection>>(DEFAULT_CAPACITY));
	}

	public JdbcWorkers(JdbcClient jdbc, BlockingQueue<Operation<Connection>> queue) {
		this.queue = queue;

		int workersN = Conf.JDBC.entry("workers").or(ConfigUtil.cpus());
		long batchTimeMs = Conf.JDBC.entry("batchTimeMs").or(5000);

		this.workers = new JdbcWorker[workersN];

		for (int i = 0; i < workers.length; i++) {
			workers[i] = new JdbcWorker(jdbc, queue, batchTimeMs);
			workers[i].start();
		}

		Log.info("Started JDBC workers", "workers", workersN, "batchTimeMs", batchTimeMs);
	}

	public void execute(Operation<Connection> operation) {
		try {
			queue.put(operation);
		} catch (InterruptedException e) {
			throw new CancellationException();
		}
	}

}
