/*-
 * #%L
 * rapidoid-http-server
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
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

package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.http.Req;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.LoginProvider;
import org.rapidoid.http.customize.RolesProvider;
import org.rapidoid.u.U;

import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class TokenAuth extends RapidoidThing {

    public static UserInfo login(Req req, String username, String password) {

        LoginProvider loginProvider = Customization.of(req).loginProvider();
        U.must(loginProvider != null, "A login provider wasn't set!");

        RolesProvider rolesProvider = Customization.of(req).rolesProvider();
        U.must(rolesProvider != null, "A roles provider wasn't set!");

        try {
            boolean success = loginProvider.login(req, username, password);

            if (success) {
                Set<String> roles = rolesProvider.getRolesForUser(req, username);

                return new UserInfo(username, roles);
            }

        } catch (Throwable e) {
            throw U.rte("Login error!", e);
        }

        return null;
    }

}
