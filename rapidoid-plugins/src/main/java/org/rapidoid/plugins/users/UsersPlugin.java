package org.rapidoid.plugins.users;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.P;
import org.rapidoid.annotation.Since;
import org.rapidoid.plugins.Plugin;

/*
 * #%L
 * rapidoid-plugins
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
@Since("3.0.0")
public interface UsersPlugin extends Plugin {

	<U> U findByUsername(@P("userClass") Class<U> userClass, @P("username") String username);

	<U> U createUser(@P("userClass") Class<U> userClass, @P("username") String username, @P("passwordHash") String passwordHash, @P("name") String name, @P("email") String email,
			String oauthId, @P("oauthProvider") String oauthProvider);

}
