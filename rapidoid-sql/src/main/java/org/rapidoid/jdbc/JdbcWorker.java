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

import org.rapidoid.activity.AbstractLoopThread;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.sql.Connection;
import java.util.Queue;

@Authors("Nikolche Mihajlovski")
@Since("5.3.4")
public class JdbcWorker extends AbstractLoopThread {

	private final JdbcClient jdbc;
	private final Queue<Operation<Connection>> queue;
	private final long batchTimeMs;

	public JdbcWorker(JdbcClient jdbc, Queue<Operation<Connection>> queue, long batchTimeMs) {
		super(0); // no pause

		this.jdbc = jdbc;
		this.queue = queue;
		this.batchTimeMs = batchTimeMs;
	}

	@Override
	protected void loop() throws Exception {

		Connection conn = null;

		try {

			long since = 0;

			do {
				Operation<Connection> op = queue.poll();

				if (op != null) {

					if (conn == null) {
						// first op
						conn = jdbc.getConnection();
						since = U.time();
					}

					op.execute(conn);

				} else {
					U.sleep(1);
					break;
				}

			} while (Msc.timedOut(since, batchTimeMs));

		} catch (Exception e) {
			Log.error("JDBC worker operation error!", e);

		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
}
