package com.example;

import org.rapidoid.setup.App;
import org.rapidoid.u.U;

public class RestApiExample {

    public static void main(String[] args) {
        // This starts a HTTP server on port 8080 and defines a handler for the route GET /hello
        App app = new App(args);

        app.get("/hello").json(() -> U.map("msg", "Hello, world!"));

        app.start();
    }

}

