package org.rapidoid.openapi;

import org.rapidoid.http.impl.RouteMeta;
import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;

import java.util.Map;

public class OpenAPIDemo {

	public static void main(String[] args) {
		Setup setup = On.setup();

		RouteMeta meta = new RouteMeta();
		meta.id("test1").summary("Test 1").tags(U.set("test")).schema(test1Schema());

		On.get("/test1/").meta(meta).plain(sayHello());

		On.get("/test2/foo").plain(sayHello());
		On.get("/test2/output").plain(sayHello());
		On.post("/test2/output").plain(sayHello());
		On.delete("/test2/output").plain(sayHello());

		OpenAPI.bootstrap(setup);
	}

	private static Map<String, Object> test1Schema() {
		return U.map(
			"type", "array",
			"items", U.map("type", "string")
		);
	}

	private static String sayHello() {
		return "Hello";
	}

}
