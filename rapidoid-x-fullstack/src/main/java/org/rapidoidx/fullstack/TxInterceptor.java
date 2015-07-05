package org.rapidoidx.fullstack;

/*
 * #%L
 * rapidoid-x-fullstack
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HTTPInterceptor;
import org.rapidoid.http.HttpExchangeImpl;
import org.rapidoid.http.HttpInterception;
import org.rapidoid.http.HttpProtocol;
import org.rapidoid.lambda.Callback;
import org.rapidoidx.db.Database;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class TxInterceptor implements HTTPInterceptor {

	private final Database db;

	public TxInterceptor(Database db) {
		this.db = db;
	}

	@Override
	public void intercept(HttpInterception interception) {
		final HttpExchangeImpl x = (HttpExchangeImpl) interception.exchange();

		Callback<Void> callback = new Callback<Void>() {
			@Override
			public void onDone(Void result, Throwable error) {
				if (error != null) {
					HttpProtocol.handleError(x, error);
				}
				x.done();
			}
		};

		x.async();
		db.transaction(interception, x.isGetReq(), callback);
	}

}
