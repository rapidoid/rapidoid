package org.rapidoid.process;

import org.rapidoid.RapidoidThing;
import org.rapidoid.activity.RapidoidThread;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
@Since("5.3.0")
public class ProcessHandle extends RapidoidThing {

	private final BlockingQueue<Object> input = new ArrayBlockingQueue<Object>(100);

	private final BlockingQueue<String> output = new ArrayBlockingQueue<String>(100);

	private final BlockingQueue<String> error = new ArrayBlockingQueue<String>(100);

	private final Process process;

	private volatile boolean inputDone;
	private volatile boolean outputDone;
	private volatile boolean errorDone;

	public ProcessHandle(final Process process) {
		this.process = process;

		Thread inputProcessor = new RapidoidThread() {
			@Override
			public void run() {
				try {
					writeAll(input, process.getOutputStream());
				} finally {
					outputDone = true;
				}
			}
		};

		inputProcessor.setDaemon(true);
		inputProcessor.start();

		Thread errorProcessor = new RapidoidThread() {
			@Override
			public void run() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					long total = readInto(reader, error);
				} finally {
					errorDone = true;
				}
			}
		};

		errorProcessor.setDaemon(true);
		errorProcessor.start();

		Thread outputProcessor = new RapidoidThread() {
			@Override
			public void run() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					long total = readInto(reader, output);

				} finally {
					outputDone = true;
				}
			}
		};

		outputProcessor.setDaemon(true);
		outputProcessor.start();
	}

	private static void writeAll(BlockingQueue<Object> input, OutputStream output) {
		while (!Thread.interrupted()) {
			try {
				Object obj = input.take();

				if (obj instanceof String) {
					String s = (String) obj;
					output.write(s.getBytes());

				} else if (obj instanceof byte[]) {
					byte[] b = (byte[]) obj;
					output.write(b);

				} else {
					throw U.rte("Unsupported input object type: " + obj);
				}

				output.flush();
			} catch (Exception e) {
				Log.error("Cannot write!", e);
			}
		}
	}

	private static long readInto(BufferedReader reader, BlockingQueue<String> dest) {
		long total = 0;

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					dest.put(line);
					total++;
				} catch (InterruptedException e) {
					throw new ThreadDeath();
				}
			}
		} catch (IOException e) {
			Log.error("Cannot read!", e);
		}

		return total;
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

	public boolean isAlive() {
		return process.isAlive();
	}

	public void receive(Operation<String> outputProcessor, Operation<String> errorProcessor) {
		String s;

		int grace = 1;

		do {
			if (outputProcessor != null) {
				while ((s = output().poll()) != null) {
					Lmbd.call(outputProcessor, s);
				}
			}

			if (errorProcessor != null) {
				while ((s = error().poll()) != null) {
					Lmbd.call(errorProcessor, s);
				}
			}

			U.sleep(10);

		} while (isAlive() || !outputDone || !errorDone || (--grace) >= 0);
	}

	public void print() {
		print(true, true);
	}

	public void print(final boolean output, final boolean error) {
		receive(new Operation<String>() {
			@Override
			public void execute(String s) throws Exception {
				if (output) {
					U.print(s);
				}
			}
		}, new Operation<String>() {
			@Override
			public void execute(String s) throws Exception {
				if (error) {
					Log.error(s);
				}
			}
		});
	}

	public String out() {
		final StringBuffer sb = new StringBuffer();

		receive(new Operation<String>() {
			@Override
			public void execute(String s) throws Exception {
				sb.append(s).append("\n");
			}
		}, null);

		return sb.toString();
	}

	public String err() {
		final StringBuffer sb = new StringBuffer();

		receive(null, new Operation<String>() {
			@Override
			public void execute(String s) throws Exception {
				sb.append(s).append("\n");
			}
		});

		return sb.toString();
	}

	public static ProcessHandle startProcess(ProcessDSL params) {

		ProcessBuilder builder = new ProcessBuilder().command(params.command());

		if (params.in() != null) {
			builder.directory(params.in());
		}

		Process process;
		try {
			process = builder.start();
		} catch (IOException e) {
			throw U.rte("Cannot start process: " + U.join(" ", params.command()));
		}

		return new ProcessHandle(process);
	}

}
