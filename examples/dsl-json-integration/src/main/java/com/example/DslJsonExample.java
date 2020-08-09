package com.example;

import com.dslplatform.json.CompiledJson;
import org.rapidoid.setup.Apps;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

public class DslJsonExample {

    public static void main(String[] args) {
        DslJsonConverter dslJsonConverter = new DslJsonConverter();

        Apps.custom().jsonResponseRenderer(dslJsonConverter);
        Apps.custom().jsonRequestBodyParser(dslJsonConverter);
        Apps.custom().beanParameterFactory(dslJsonConverter);

        On.get("/hello").json(() -> U.map("msg", "Hello, world!"));
        On.post("/reflection").json((HelloReflection h) -> {
            h.x = h.x * 2;
            return h;
        });
        On.post("/compiled").json((HelloCompiled h) -> {
            h.x = h.x * 2;
            return h;
        });
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

