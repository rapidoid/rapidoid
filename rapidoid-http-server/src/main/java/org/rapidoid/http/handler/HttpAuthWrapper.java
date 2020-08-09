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

package org.rapidoid.http.handler;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.UserInfo;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.Req;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.security.Secure;
import org.rapidoid.u.U;
import org.rapidoid.util.TokenAuthData;

import java.util.Collections;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.5.1")
public class HttpAuthWrapper extends RapidoidThing implements HttpWrapper {

    private final Set<String> requiredRoles;

    public HttpAuthWrapper(Set<String> requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    @Override
    public Object wrap(final Req req, final HandlerInvocation invocation) throws Exception {
        TokenAuthData auth = HttpUtils.getAuth(req);

        String username = auth != null ? auth.user : null;

        if (U.isEmpty(username)) {
            HttpUtils.clearUserData(req);
        }

        Set<String> roles = userRoles(req, username);
        Set<String> scope = auth != null ? auth.scope : null;

        if (U.notEmpty(requiredRoles) && !Secure.hasAnyRole(username, roles, requiredRoles)) {
            throw new SecurityException("The user doesn't have the required roles!");
        }

        Ctx ctx = Ctxs.required();
        ctx.setUser(new UserInfo(username, roles, scope));

        return invocation.invoke();
    }

    private Set<String> userRoles(Req req, String username) {
        if (username != null) {
            try {
                return Customization.of(req).rolesProvider().getRolesForUser(req, username);
            } catch (Exception e) {
                throw U.rte(e);
            }
        } else {
            return Collections.emptySet();
        }
    }

}
