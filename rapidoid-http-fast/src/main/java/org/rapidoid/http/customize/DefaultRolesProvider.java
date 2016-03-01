package org.rapidoid.http.customize;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class DefaultRolesProvider implements RolesProvider {

	@Override
	public Set<String> getRolesForUser(String username) throws Exception {
		return null; // FIXME
	}

}
