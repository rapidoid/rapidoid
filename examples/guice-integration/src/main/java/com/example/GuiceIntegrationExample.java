package com.example;

import org.rapidoid.http.Self;
import org.rapidoid.integrate.GuiceBeans;
import org.rapidoid.integrate.Integrate;
import org.rapidoid.log.Log;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;

public class GuiceIntegrationExample {

	public static void main(String[] args) {
		Log.info("Starting application");

		// disable Rapidoid's hot class reloading, it doesn't play well with Guice
		On.changes().ignore();

		GuiceBeans beans = Integrate.guice(new WebModule());
		App.register(beans);

		// test the RESTful service
		Self.get("/add?x=6&y=4").print();
		Self.get("/add?x=1&y=22").expect().entry("sum", 23);

		// usually you wouldn't shutdown the application
		App.shutdown();
	}

}

