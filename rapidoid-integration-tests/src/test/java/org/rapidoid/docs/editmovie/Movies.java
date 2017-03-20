package org.rapidoid.docs.editmovie;

import org.rapidoid.annotation.Controller;
import org.rapidoid.annotation.Page;
import org.rapidoid.docs.showmovie.Movie;
import org.rapidoid.gui.Btn;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.input.Form;

/*
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

@Controller
public class Movies {

	@Page("/")
	public Object movie() {
		org.rapidoid.docs.showmovie.Movie movie = new Movie();
		movie.title = "Chappie";
		movie.year = 2015;

		Btn save = GUI.btn("Save").primary();
		Form form = GUI.edit(movie).buttons(save);
		return GUI.page(form).brand("Edit movie details");
	}

}
