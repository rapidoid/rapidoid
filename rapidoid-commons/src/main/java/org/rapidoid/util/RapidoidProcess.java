package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
@Since("4.1.0")
public class RapidoidProcess extends RapidoidThing {

	private final String[] command;

	private final BlockingQueue<Object> input = new ArrayBlockingQueue<Object>(100000);

	private final BlockingQueue<String> output = new ArrayBlockingQueue<String>(100000);

	private final BlockingQueue<String> error = new ArrayBlockingQueue<String>(100000);

	private final Process process;

	public RapidoidProcess(String[] command) {
		this.command = command;

		this.process = startProcess();
	}

	private Process startProcess() {
		ProcessBuilder builder = new ProcessBuilder().command(command);
		try {
			return builder.start();
		} catch (IOException e) {
			throw U.rte("Cannot start process: " + U.join(" ", command));
		}
	}

	public void init() throws Exception {

		new Thread() {
			public void run() {
				while (!Thread.interrupted()) {
					try {
						Object obj = input.take();

						if (obj instanceof String) {
							String s = (String) obj;
							process.getOutputStream().write(s.getBytes());
						} else if (obj instanceof byte[]) {
							byte[] b = (byte[]) obj;
							process.getOutputStream().write(b);
						} else {
							throw U.rte("Unsupported input object type: " + obj);
						}

						process.getOutputStream().flush();
					} catch (Exception e) {
						Log.error("Cannot write!", e);
					}
				}
			}
		}.start();

		new Thread() {
			@SuppressWarnings("unused")
			public void run() {
				long total = 0;
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				try {
					String line;
					while ((line = reader.readLine()) != null) {
						try {
							error.put(line);
							total++;
						} catch (InterruptedException e) {
							throw new ThreadDeath();
						}
					}
				} catch (IOException e) {
					Log.error("Cannot read!", e);
				}
			}

			;
		}.start();

		new Thread() {
			@SuppressWarnings("unused")
			public void run() {
				long total = 0;
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				try {
					String line;
					while ((line = reader.readLine()) != null) {
						try {
							output.put(line);
							total++;
						} catch (InterruptedException e) {
							throw new ThreadDeath();
						}
					}
				} catch (IOException e) {
					Log.error("Cannot read!", e);
				}
			}

			;
		}.start();

	}

	public BlockingQueue<Object> input() {
		return input;
	}

	public BlockingQueue<String> output() {
		return output;
	}

	public BlockingQueue<String> error() {
		return error;
	}

	public Process process() {
		return process;
	}

}
