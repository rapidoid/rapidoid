package issues;

import org.rapidoid.app.Screen;
import org.rapidoid.db.DB;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HomeScreen extends Screen {

	public Object content() {
		return render("home.html", "count", DB.getAll(Issue.class).size());
	}

}
