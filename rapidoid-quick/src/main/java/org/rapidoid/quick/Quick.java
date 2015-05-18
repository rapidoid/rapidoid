package org.rapidoid.quick;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.rapidoid.app.Apps;
import org.rapidoid.jpa.dbplugin.JPADBPlugin;
import org.rapidoid.util.U;

public class Quick {

	public static void main(String[] args) {
		run((Object[]) args);
	}

	public static void run(Object... args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu-main-h2");
		EntityManager em = emf.createEntityManager();

		JPADBPlugin db = new JPADBPlugin(em);

		List<Object> appArgs = U.<Object> list(db);
		appArgs.addAll(U.list(args));

		Apps.run(U.array(appArgs));
	}

}
