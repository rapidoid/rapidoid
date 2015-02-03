package issues;

import org.rapidoid.annotation.DbEntity;
import org.rapidoid.annotation.Optional;
import org.rapidoid.annotation.Scaffold;

@Scaffold
@DbEntity
@Authors("Nikolche Mihajlovski")
public class Issue {

	public long id;

	public String title;

	public Priority priority = Priority.MEDIUM;

	@Optional
	public String description;

}
