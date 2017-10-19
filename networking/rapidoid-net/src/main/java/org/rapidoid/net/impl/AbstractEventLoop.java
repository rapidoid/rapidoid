package org.rapidoid.net.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class AbstractEventLoop<T> extends AbstractLoop<T> {

	protected final Selector selector;

	protected volatile long approxTime = U.time();

	public AbstractEventLoop(String name) {
		super(name);

		Selector sel;

		try {
			sel = Selector.open();
		} catch (IOException e) {
			Log.error("Cannot open selector!", e);
			throw new RuntimeException(e);
		}

		this.selector = sel;
	}

	private void processKey(SelectionKey key) {
		if (key == null || !key.isValid()) {
			return;
		}

		if (key.isAcceptable()) {
			Log.trace("accepting", "key", key);

			try {
				acceptOP(key);
			} catch (IOException e) {
				failedOP(key, e);
				Log.error("accept IO error for key: " + key, e);
			} catch (Throwable e) {
				failedOP(key, e);
				Log.error("accept failed for key: " + key, e);
			}

		} else if (key.isConnectable()) {
			Log.trace("connection event", "key", key);

			try {
				connectOP(key);
			} catch (IOException e) {
				failedOP(key, e);
				Log.error("connect IO error for key: " + key, e);
			} catch (Throwable e) {
				failedOP(key, e);
				Log.error("connect failed for key: " + key, e);
			}
		} else if (key.isReadable()) {
			Log.trace("reading", "key", key);

			try {
				readOP(key);
			} catch (IOException e) {
				failedOP(key, e);
				Log.error("read IO error for key: " + key, e);
			} catch (Throwable e) {
				failedOP(key, e);
				Log.error("read failed for key: " + key, e);
			}

		} else if (key.isWritable()) {
			Log.trace("writing", "key", key);

			try {
				writeOP(key);
			} catch (IOException e) {
				failedOP(key, e);
				Log.error("write IO error for key: " + key, e);
			} catch (Throwable e) {
				failedOP(key, e);
				Log.error("write failed for key: " + key, e);
			}
		}
	}

	@Override
	protected final void insideLoop() {

		approxTime = U.time();

		try {
			doProcessing();
		} catch (Throwable e) {
			Log.error("Event processing error!", e);
		}

		try {
			selector.select(50);
		} catch (IOException e) {
			Log.error("Select failed!", e);
		}

		approxTime = U.time();

		try {
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			synchronized (selectedKeys) {

				Iterator<?> iter = selectedKeys.iterator();

				while (iter.hasNext()) {
					SelectionKey key = (SelectionKey) iter.next();
					iter.remove();

					processKey(key);
				}
			}
		} catch (ClosedSelectorException e) {
			// do nothing
		}
	}

	protected abstract void doProcessing();

	protected void acceptOP(SelectionKey key) throws IOException {
		throw new RuntimeException("Accept operation is not implemented!");
	}

	protected void connectOP(SelectionKey key) throws IOException {
		throw new RuntimeException("Connect operation is not implemented!");
	}

	protected void readOP(SelectionKey key) throws IOException {
		throw new RuntimeException("Accept operation is not implemented!");
	}

	protected void writeOP(SelectionKey key) throws IOException {
		throw new RuntimeException("Accept operation is not implemented!");
	}

	protected void failedOP(SelectionKey key, Throwable e) {
		// ignore the errors by default
	}

}
