/*-
 * #%L
 * rapidoid-rest
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

package org.rapidoid.validation;

import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.*;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.setup.Apps;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;

import javax.validation.constraints.NotNull;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpBeanValidationTest extends IsolatedIntegrationTest {

    @Test
    public void testValidation() {
        My.validator(new JavaxBeanValidator());
        Apps.beans(new FooCtrl());

        onlyGet("/echo?num=123");
        onlyGet("/echo");

        onlyGet("/validating?num=123");
        onlyGet("/validating");

        onlyPost("/save?num=123");
        onlyPost("/save");
    }

    @Test
    public void testCustomValidation() {
        On.get("/invalid1").html((Bar bar) -> "ok");
        On.get("/invalid2").json((Bar bar) -> "ok");

        My.validator(new JavaxBeanValidator());

        onlyGet("/invalid1?err");
        onlyGet("/invalid2?err");

        My.validator((req, bean) -> "My validation error!");

        onlyGet("/invalid1?val");
        onlyGet("/invalid2?val");

        My.validator(null);

        onlyGet("/invalid1?inv");
        onlyGet("/invalid2?inv");
    }

}

class Foo {

    public Long id;

    @NotNull
    public Long num;

    public Object getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

class FooCtrl {

    @GET
    public Foo echo(Foo foo) {
        return foo;
    }

    @GET
    public Foo validating(@Valid Foo foo) {
        return foo;
    }

    @POST
    public Foo save(@Valid Foo foo) {
        return foo;
    }

}

@Valid
class Bar {
    @NotNull
    public Long x;
}
