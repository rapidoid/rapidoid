package org.rapidoid.group;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Str;
import org.rapidoid.u.U;

import java.lang.reflect.Method;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public abstract class AbstractManageable extends RapidoidThing implements Manageable {

	@Override
	public Object execute(String action) {
		Method method = Cls.findMethod(getClass(), Str.uncapitalized(action));

		if (method != null) {
			return Cls.invoke(method, this);

		} else {
			return executeAction(action);
		}
	}

	protected Object executeAction(String action) {
		throw U.rte("Cannot handle action '%s'!", action);
	}

}
