package com.example;

import org.rapidoid.setup.App;
import org.rapidoid.u.U;

public class HelloWorldExample {

    public static void main(String[] args) {
        App app = new App();

        // define a handler for the route GET /hello
        app.get("/hello").json(() -> U.map("msg", "Hello, world!"));

        // start a HTTP server on port 8080 and
        app.start();
    }

}

