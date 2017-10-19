package org.rapidoid.dates;

/*
 * #%L
 * rapidoid-commons
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

import org.junit.Test;
import org.rapidoid.commons.Dates;
import org.rapidoid.test.AbstractCommonsTest;

import java.util.Calendar;

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class DatesTest extends AbstractCommonsTest {

	@Test
	public void testDate() {
		eq(Dates.date("27.12.2001"), Dates.date(27, 12, 2001));
		eq(Dates.date("2/11.2000"), Dates.date(2, 11, 2000));
		eq(Dates.date("20-03/1984"), Dates.date(20, 3, 1984));
		eq(Dates.date("2006-12-31"), Dates.date(31, 12, 2006));

		Calendar cal = Calendar.getInstance();
		cal.setTime(Dates.date(31, 12, 2006));

		eq(cal.get(Calendar.YEAR), 2006);
		eq(cal.get(Calendar.MONTH), 11);
		eq(cal.get(Calendar.DAY_OF_MONTH), 31);

		cal.setTime(Dates.date("20/01"));

		eq(cal.get(Calendar.YEAR), Dates.thisYear());
		eq(cal.get(Calendar.MONTH), 0);
		eq(cal.get(Calendar.DAY_OF_MONTH), 20);
	}

}
