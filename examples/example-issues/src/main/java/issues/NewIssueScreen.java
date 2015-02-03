package issues;

import org.rapidoid.annotation.Session;
import org.rapidoid.app.Screen;
import org.rapidoid.db.DB;
import org.rapidoid.html.Tag;
import org.rapidoid.widget.FormWidget;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class NewIssueScreen extends Screen {

	@Session
	public Issue issue = new Issue();

	public Object content() {
		Tag title = h2("Add new issue").style("margin-bottom:15px");
		FormWidget form = create(issue).buttons(SAVE, CANCEL);
		return mid6(title, form);
	}

	public void onSave() {
		DB.insert(issue);
		issue = new Issue();
	}

	public void onCancel() {
		issue = new Issue();
	}

}
