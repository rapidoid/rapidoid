package demo.config;


import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.rapidoid.app.GUI;
import org.rapidoid.fluent.Do;
import org.rapidoid.fluent.Find;
import org.rapidoid.fluent.Flow;
import org.rapidoid.gui.Btn;
import org.rapidoid.gui.menu.PageMenu;
import org.rapidoid.http.REST;
import org.rapidoid.http.fast.On;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

public class Oneliners extends GUI {

	public static void main(String[] args) {

		Map<String, String> cfg = U.map("title", "App", "user", "u1", "theme", "x");

		On.get("/cfg").json(cfg);

		PageMenu menu = PageMenu.from(U.map("Home", "/", "Groups", "/group"));

		On.defaultWrap((data, next) -> next.invoke(x -> page(x).menu(menu).title(cfg.get("title"))));

		List<Btn> letters = Flow.chars('a', 'z').map(c -> btn(c).linkTo("/find?p=" + c)).toList();

		On.page("/").gui("p", p -> U.list(letters, grid(cfg).keyView(k -> a(k).href("/edit?key=" + k))));

		On.page("/group").gui("x", x -> {
			return Do.map(Do.group(cfg).by((k, v) -> k.charAt(0))).toList((k, v) -> U.list(h3(k), grid(v)));
		});

		On.page("/find").gui("p", p -> U.list(letters, grid(Find.allOf(cfg).where((k, v) -> k.startsWith(p)))));

		Btn ok = btn("Update").onClick(() -> Log.info("Updated", "cfg", cfg));

		On.page("/edit").gui("key", key -> edit(cfg, key).buttons(ok, btn("Back").linkTo("/")));

		Runnable replicate = () -> REST.get("http://localhost:8888/cfg", Object.class, U::print);

		Jobs.scheduleAtFixedRate(replicate, 3, 3, TimeUnit.SECONDS);
	}

}
