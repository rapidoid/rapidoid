package org.rapidoid.openapi;

import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;

public class OpenAPIDemo {

	public static void main(String[] args) {
		Setup setup = On.setup();

		On.get("/test1/").plain(sayHello());
		On.get("/test2/foo").plain(sayHello());
		On.get("/test2/output").plain(sayHello());
		On.post("/test2/output").plain(sayHello());
		On.delete("/test2/output").plain(sayHello());

		OpenAPI.bootstrap(setup);
	}

	private static String sayHello() {
		return "Hello";
	}

}
