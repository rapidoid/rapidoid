package org.rapidoid.openapi;

import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;

public class OpenAPIDemo {

    public static void main(String[] args) {
        Setup setup = On.setup();

        On.get("/test1/").plain(OpenAPIDemo.sayHello());
        On.get("/test2/saida").plain(OpenAPIDemo.sayHello());
        On.get("/test2/output").plain(OpenAPIDemo.sayHello());
        On.post("/test2/output").plain(OpenAPIDemo.sayHello());
        On.delete("/test2/output").plain(OpenAPIDemo.sayHello());

        OpenAPI.register(setup);
        setup.activate();
    }

    public static String sayHello() {
        return "Hello";
    }

}
