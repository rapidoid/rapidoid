package org.rapidoid.http;

/*
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

import org.junit.Test;
import org.rapidoid.annotation.*;
import org.rapidoid.jpa.JPA;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Authors("Nikolche Mihajlovski")
@Since("5.4.2")
public class TxErrorHandlerTest extends IsolatedIntegrationTest {

	@Test
	public void txWrapperShouldNotDisruptCustomErrorHandling() {
		JPA.bootstrap(path());

		App.beans(new TxCtrl());

		On.error(IllegalArgumentException.class).handler((req, resp, e) -> {
			resp.code(400);
			return U.map("error", "Invalid data!");
		});

		onlyGet("/x");
	}

	@Entity
	static class Foo {
		@Id
		@GeneratedValue
		public Integer id;

		public String name;
	}

	@Controller
	static class TxCtrl {
		@Transaction
		@GET
		public void x() {
			throw new IllegalArgumentException();
		}
	}

}
