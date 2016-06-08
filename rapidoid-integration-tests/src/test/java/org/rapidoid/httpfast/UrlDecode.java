package org.rapidoid.httpfast;

import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.setup.On;

/**
 * Created by qlyine on 6/8/16.
 */
public class UrlDecode {
    public static void main(String[] args) {
        On.address("0.0.0.0").port(8080);
        On.get("/ping").serve(new ReqHandler() {
            @Override
            public Object execute(Req req) throws Exception {
                System.out.println(req);
                System.out.println(req.uri());
                System.out.println(req.params());
                return req.params();
            }
        });
    }
}
