package org.rapidoidx.net.impl;

/*
 * #%L
 * rapidoid-x-net
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.Resetable;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class ConnState implements Resetable {

	public volatile long n;

	public volatile Object obj;

	public ConnState() {}

	public ConnState(long n, Object obj) {
		this.n = n;
		this.obj = obj;
	}

	/* COMMENTED OUT FOR PRODUCTION (the log is used for debugging during development): */

	// private final List<String> log = Collections.synchronizedList(U.<String> list());

	public void reset() {
		n = 0;
		obj = null;
		log("<<< RESET >>>");
	}

	/* The log is used for debugging during development. */
	public void log(String msg) {
		/* COMMENTED OUT FOR PRODUCTION: */
		// log.add(msg);
	}

	/* The log is used for debugging during development. */
	public List<String> log() {
		return null;

		/* COMMENTED OUT FOR PRODUCTION: */
		// return log;
	}

	public ConnState copy() {
		return new ConnState(n, obj);
	}

	public void copyFrom(ConnState state) {
		this.n = state.n;
		this.obj = state.obj;
	}

}
