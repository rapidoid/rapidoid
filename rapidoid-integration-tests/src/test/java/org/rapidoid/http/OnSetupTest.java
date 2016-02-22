package org.rapidoid.http;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.data.JSON;
import org.rapidoid.data.YAML;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class OnSetupTest extends HttpTestCommons {

	@Test
	public void testSerializationConfig() {
		On.renderJson(JSON::stringify);
		On.parseJson(JSON::parse);

		On.renderYaml(YAML::stringify);
		On.parseYaml(YAML::parse);

		On.login((username, password) -> password.equals(username + "!"));
		On.rolesOf(username -> username.equals("root") ? U.set("admin") : U.set());
	}

}
