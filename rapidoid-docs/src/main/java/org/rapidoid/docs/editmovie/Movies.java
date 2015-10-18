package org.rapidoid.docs.editmovie;

import org.rapidoid.annotation.Page;
import org.rapidoid.annotation.Web;
import org.rapidoid.app.GUI;
import org.rapidoid.docs.showmovie.Movie;
import org.rapidoid.widget.ButtonWidget;

/*
 * #%L
 * rapidoid-docs
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

@Web
public class Movies {

	@Page(uri = "/", title = "Edit movie details")
	public Object movie() {
		Movie movie = new Movie();
		movie.title = "Chappie";
		movie.year = 2015;

		ButtonWidget save = GUI.btn("Save").primary();
		return GUI.edit(movie).buttons(save);
	}

}
