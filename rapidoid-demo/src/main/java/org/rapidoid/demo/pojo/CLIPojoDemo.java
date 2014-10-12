package org.rapidoid.demo.pojo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.rapidoid.pojo.POJO;
import org.rapidoid.pojo.PojoDispatcher;
import org.rapidoid.pojo.PojoRequest;
import org.rapidoid.pojo.PojoResponse;
import org.rapidoid.pojo.impl.PojoRequestImpl;
import org.rapidoid.util.JSON;
import org.rapidoid.util.U;

public class CLIPojoDemo {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Throwable {
		U.args(args);

		PojoDispatcher dispatcher = POJO.serviceDispatcher();

		System.out.println("HELLO");

		U.setLogLevel(U.DEBUG);

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while ((line = reader.readLine()) != null) {
			int p1 = line.indexOf(' ');
			int p2 = line.indexOf(' ', p1 + 1);
			Map<String, String> extra = p2 > 0 ? JSON.parse(line.substring(p2 + 1), Map.class) : U.map();
			PojoRequest req = new PojoRequestImpl(line.substring(0, p1), p2 > 0 ? line.substring(p1 + 1, p2)
					: line.substring(p1 + 1), extra);
			U.debug("processing request", "request", req);
			PojoResponse resp = dispatcher.dispatch(req);
			System.out.println(resp.getResult());
		}

		System.out.println("BYE");
	}

}
