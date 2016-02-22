package org.rapidoid.setup;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public interface RolesProvider {

	Set<String> getRolesForUser(String username);

}
