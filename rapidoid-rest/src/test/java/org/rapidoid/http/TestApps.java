package org.rapidoid.http;

import org.rapidoid.crypto.Crypto;
import org.rapidoid.log.Log;
import org.rapidoid.setup.App;
import org.rapidoid.u.U;

public class TestApps {

    public static App defaultTestApp() {
        App app = new App().start();

        app.get("/echo").serve((Req x) -> {
            x.response().contentType(MediaType.PLAIN_TEXT_UTF_8);
            return x.verb() + ":" + x.path() + ":" + x.query();
        });

        app.get("/hello").html("Hello");

        app.post("/upload").plain((Req x) -> {
            Log.info("Uploaded files", "files", x.files().keySet());

            boolean hasF3 = x.files().containsKey("f3");

            return U.join(":", x.cookies().get("foo"), x.cookies().get("COOKIE1"), x.posted().get("a"), x.files().size(),
                    Crypto.md5(x.file("f1").content()),
                    Crypto.md5(x.files().get("f2").get(0).content()),
                    Crypto.md5(hasF3 ? x.file("f3").content() : new byte[0]));
        });

        app.req((Req x) -> x.response().html(U.join(":", x.verb(), x.path(), x.query())));

        return app;
    }

}
