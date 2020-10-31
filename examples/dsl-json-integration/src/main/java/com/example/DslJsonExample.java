package com.example;

import com.dslplatform.json.CompiledJson;
import org.rapidoid.setup.App;
import org.rapidoid.u.U;

public class DslJsonExample {

    public static void main(String[] args) {
        App app = new App(args);

        DslJsonConverter dslJsonConverter = new DslJsonConverter();

        app.custom().jsonResponseRenderer(dslJsonConverter);
        app.custom().jsonRequestBodyParser(dslJsonConverter);
        app.custom().beanParameterFactory(dslJsonConverter);

        app.get("/hello").json(() -> U.map("msg", "Hello, world!"));

        app.post("/reflection").json((HelloReflection h) -> {
            h.x = h.x * 2;
            return h;
        });

        app.post("/compiled").json((HelloCompiled h) -> {
            h.x = h.x * 2;
            return h;
        });

        app.start();
    }

    public static class HelloReflection {
        public int x;
        public String s;
    }

    @CompiledJson
    public static class HelloCompiled {
        public int x;
        public String s;
    }
}

