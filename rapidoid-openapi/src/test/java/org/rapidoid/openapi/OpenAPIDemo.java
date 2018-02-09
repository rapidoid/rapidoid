package org.rapidoid.openapi;

import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;

public class OpenAPIDemo {

    public static void main(String[] args) {
        Setup setup = On.setup();
        OpenAPI.register(setup);
        setup.activate();
    }

}
