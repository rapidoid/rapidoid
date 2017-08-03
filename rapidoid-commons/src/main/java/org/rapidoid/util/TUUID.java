package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.u.U;

import java.nio.ByteBuffer;
import java.util.UUID;

/*
 * #%L
 * rapidoid-commons
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
@Since("5.3.0")
public final class TUUID extends RapidoidThing implements Comparable<TUUID> {

	private final long time;

	private final long uuidHigh;

	private final long uuidLow;

	public TUUID() {
		UUID uuid = UUID.randomUUID();
		this.time = U.time();
		this.uuidHigh = uuid.getMostSignificantBits();
		this.uuidLow = uuid.getLeastSignificantBits();
	}

	public TUUID(long time, long uuidHigh, long uuidLow) {
		this.time = time;
		this.uuidHigh = uuidHigh;
		this.uuidLow = uuidLow;
	}

	public TUUID(long time, String id) {
		this.time = time;

		byte[] hash = Crypto.md5Bytes(id.getBytes());
		ByteBuffer buf = ByteBuffer.wrap(hash);

		this.uuidHigh = buf.getLong();
		this.uuidLow = buf.getLong();
	}

	public long time() {
		return time;
	}

	public long uuidHigh() {
		return uuidHigh;
	}

	public long uuidLow() {
		return uuidLow;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TUUID tuuid = (TUUID) o;

		if (time != tuuid.time) return false;
		if (uuidHigh != tuuid.uuidHigh) return false;
		return uuidLow == tuuid.uuidLow;
	}

	@Override
	public int hashCode() {
		int result = (int) (time ^ (time >>> 32));
		result = 31 * result + (int) (uuidHigh ^ (uuidHigh >>> 32));
		result = 31 * result + (int) (uuidLow ^ (uuidLow >>> 32));
		return result;
	}

	@Override
	public int compareTo(TUUID other) {

		int cmp = cmp(this.time, other.time);
		if (cmp != 0) return cmp;

		cmp = cmp(this.uuidHigh, other.uuidHigh);
		if (cmp != 0) return cmp;

		cmp = cmp(this.uuidLow, other.uuidLow);
		if (cmp != 0) return cmp;

		return 0;
	}

	private static int cmp(long a, long b) {
		return (a < b) ? -1 : (a > b ? 1 : 0);
	}

	public byte[] toBytes() {
		ByteBuffer buf = ByteBuffer.wrap(new byte[24]);

		buf.putLong(time);
		buf.putLong(uuidHigh);
		buf.putLong(uuidLow);

		return buf.array();
	}

	public static TUUID fromBytes(byte[] bytes) {
		U.must(bytes.length == 24, "Expected 24 bytes, got: %s", bytes.length);
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		return new TUUID(buf.getLong(), buf.getLong(), buf.getLong());
	}

	@Override
	public String toString() {
		return Str.toWebSafeBase64(toBytes());
	}

	public static TUUID fromString(String tuuid) {
		U.notNull(tuuid, "TUUID");
		return fromBytes(Str.fromWebSafeBase64(tuuid));
	}
}
