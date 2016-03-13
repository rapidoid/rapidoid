package org.rapidoid.util;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
public interface Role {

	String ANYBODY = "ANYBODY";

	String GUEST = "GUEST";

	String ADMIN = "ADMINISTRATOR";

	String MANAGER = "MANAGER";

	String MODERATOR = "MODERATOR";

	String LOGGED_IN = "LOGGED_IN";

	String OWNER = "OWNER";

	String SHARED_WITH = "SHARED_WITH";

	String RESTARTER = "RESTARTER";

}
