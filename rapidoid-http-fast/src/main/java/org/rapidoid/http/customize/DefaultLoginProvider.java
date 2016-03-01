package org.rapidoid.http.customize;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DefaultLoginProvider implements LoginProvider {
	@Override
	public boolean login(String username, String password) throws Exception {
		return false; // FIXME
	}
}
