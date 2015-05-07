package org.rapidoid.demo.taskplanner.model;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.DbEntity;
import org.rapidoid.annotation.Display;
import org.rapidoid.annotation.Optional;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.entity.Entity;
import org.rapidoid.extra.domain.LowHigh3;
import org.rapidoid.security.annotation.CanChange;

/*
 * #%L
 * rapidoid-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

@Scaffold
@DbEntity
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Task extends Entity {

	@Display
	@CanChange({ MODERATOR, OWNER })
	public String title;

	@Display
	@CanChange({ MODERATOR, OWNER, SHARED_WITH })
	public LowHigh3 priority = LowHigh3.MEDIUM;

	@Optional
	@CanChange({ MODERATOR, OWNER, SHARED_WITH })
	public String description;

	public int rating;

}
