package org.rapidoid.inmem;

/*
 * #%L
 * rapidoid-inmem
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.rapidoid.beany.PropertySelector;
import org.rapidoid.lambda.Callback;
import org.rapidoid.util.Tuple;

public class InMemData {

	final long startedAt = System.currentTimeMillis();

	final String filename;

	final EntitySerializer serializer;

	final EntityConstructor constructor;

	final PropertySelector relPropSelector;

	final ConcurrentMap<Tuple, RelPair> relPairs = new ConcurrentHashMap<Tuple, RelPair>();

	final AtomicLong ids = new AtomicLong();

	final AtomicLong lastChangedOn = new AtomicLong();

	final AtomicLong lastPersistedOn = new AtomicLong();

	final AtomicBoolean active = new AtomicBoolean(true);

	final AtomicBoolean aOrB = new AtomicBoolean(true);

	final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	final AtomicBoolean insideTx = new AtomicBoolean(false);

	final ConcurrentNavigableMap<Long, Rec> txChanges = new ConcurrentSkipListMap<Long, Rec>();

	final ConcurrentNavigableMap<Long, Object> txInsertions = new ConcurrentSkipListMap<Long, Object>();

	final ConcurrentLinkedQueue<Callback<Void>> txCallbacks = new ConcurrentLinkedQueue<Callback<Void>>();

	final AtomicLong txIdCounter = new AtomicLong();

	volatile ConcurrentNavigableMap<Long, Rec> prevData = new ConcurrentSkipListMap<Long, Rec>();

	volatile ConcurrentNavigableMap<Long, Rec> data = new ConcurrentSkipListMap<Long, Rec>();

	public InMemData(String filename, EntitySerializer serializer, EntityConstructor constructor,
			PropertySelector relPropSelector) {
		this.filename = filename;
		this.serializer = serializer;
		this.constructor = constructor;
		this.relPropSelector = relPropSelector;
	}

}
