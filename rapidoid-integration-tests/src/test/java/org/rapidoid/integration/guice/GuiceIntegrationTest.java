package org.rapidoid.integration.guice;

import org.junit.Test;
import org.rapidoid.http.IntegrationTestCommons;
import org.rapidoid.http.Self;
import org.rapidoid.integrate.Integrate;
import org.rapidoid.ioc.Beans;
import org.rapidoid.setup.App;

public class GuiceIntegrationTest extends IntegrationTestCommons {

	@Test
	public void testGuiceIntegration() {
		Beans beans = Integrate.guice(new MathModule());
		App.register(beans);

		Self.get("/add?x=6&y=4").expect().entry("sum", 10);
		Self.get("/add?x=1&y=22").expect().entry("sum", 23);
	}

}

