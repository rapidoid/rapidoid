package org.rapidoid.reload;

import org.rapidoid.http.Req;
import org.rapidoid.web.On;

/**
 * Demo for class reloading. E.g. try changing the Abc class...
 */
public class ReloadDemo {

	public static void main(String[] args) {
		On.bootstrap();

		On.changes().reload();
//		On.changes().restart();

		On.get("/aa").json((Req req, String x) -> x + ":" + req);
	}

}
