package org.rapidoidx.inmem;

/*
 * #%L
 * rapidoid-x-inmem
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.PropertySelector;
import org.rapidoid.lambda.Callback;
import org.rapidoid.tuple.Tuple;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class InMemData implements Serializable {

	private static final long serialVersionUID = 4124579290874253214L;

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

	final AtomicBoolean txReadonly = new AtomicBoolean();

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
