package custom.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.util.U;
import org.rapidoidx.db.XDB;

import custom.rapidoidx.db.model.Person;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbStatisticalTest extends DbTestCommons {

	class Op {
		final int op = rnd(3);
		final String name = rndStr(0, 100);
		final int age = rnd();
		final long id = rnd((int) (XDB.size() * 2) + 10);
		final Person person = new Person(name, age);
		final boolean fail = rnd(30) == 0;
	}

	class Ret {
		long id = -1;
		boolean illegalId = false;
		boolean ok = false;
	}

	private final Map<Long, Object> persons = U.synchronizedMap();

	@Test
	public void testDbOperations() {

		Log.setLogLevel(LogLevel.ERROR);

		multiThreaded(Conf.cpus(), 50000, new Runnable() {

			@Override
			public synchronized void run() {

				int n = rnd(10) + 1;
				final Op[] ops = new Op[n];
				final Ret[] rets = new Ret[n];

				for (int i = 0; i < ops.length; i++) {
					ops[i] = new Op();
					rets[i] = new Ret();
				}

				if (yesNo()) {

					final AtomicBoolean complete = new AtomicBoolean(false);

					XDB.transaction(new Runnable() {
						@Override
						public void run() {
							for (int i = 0; i < ops.length; i++) {

								try {
									doDbOp(ops[i], rets[i]);
								} catch (IllegalArgumentException e) {
									rets[i].illegalId = true;
									throw U.rte(e);
								}

								if (ops[i].fail) {
									throw U.rte("err");
								}
							}

							complete.set(true);
						}
					}, false, null);

					for (int i = 0; i < ops.length; i++) {
						if (ops[i].fail || !rets[i].ok || rets[i].illegalId) {
							return;
						}
					}

					U.must(complete.get());

					for (int i = 0; i < ops.length; i++) {
						doShadowOp(ops[i], rets[i]);
					}

				} else {
					try {
						doDbOp(ops[0], rets[0]);
					} catch (IllegalArgumentException e) {
						rets[0].illegalId = true;
					}

					doShadowOp(ops[0], rets[0]);
				}
			}
		});

		XDB.shutdown();

		System.out.println("Comparing data...");
		compareData();
		System.out.println("Total " + persons.size() + " records.");
	}

	private void compareData() {
		eq(XDB.size(), persons.size());

		XDB.each(new Operation<Person>() {
			@Override
			public void execute(Person p) throws Exception {
				Person p2 = (Person) persons.get(num(p.id()));
				notNull(p2);
				eq(p2.id(), p.id());
				eq(p2.age, p.age);
				eq(p2.name, p.name);
			}
		});
	}

	private void doDbOp(Op op, Ret ret) {
		switch (op.op) {
		case 0:
			ret.id = XDB.insert(op.person);
			break;

		case 1:
			XDB.delete(op.id);
			break;

		case 2:
			op.person.version(XDB.getVersionOf(op.id));
			XDB.update(op.id, op.person);
			break;

		default:
			throw U.notExpected();
		}
		ret.ok = true;
	}

	private void doShadowOp(Op op, Ret ret) {
		switch (op.op) {
		case 0:
			isNull(persons.put(ret.id, op.person));
			break;

		case 1:
			if (!ret.illegalId) {
				notNull(persons.remove(op.id));
			} else {
				isFalse(persons.containsKey(op.id));
			}
			break;

		case 2:
			if (!ret.illegalId) {
				notNull(persons.put(op.id, op.person));
			} else {
				isFalse(persons.containsKey(op.id));
			}
			break;

		default:
			throw U.notExpected();
		}
	}

}
