package org.rapidoid.http;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.Log;
import org.rapidoid.scan.Scan;

import java.lang.annotation.Annotation;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class OnAnnotated {

	private final Class<? extends Annotation>[] annotated;

	private volatile String[] packages;

	OnAnnotated(Class<? extends Annotation>[] annotated, String pkg) {
		this.annotated = annotated;
		this.packages = new String[]{pkg};
	}

	public synchronized void forEach(Operation<Class<?>> classOperation) {
		for (Class<?> cls : getAll()) {
			try {
				classOperation.execute(cls);
			} catch (Exception e) {
				Log.error("Cannot process annotated class!", e);
			}
		}
	}

	public synchronized OnAnnotated in(String... packages) {
		this.packages = packages;
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized List<Class<?>> getAll() {
		return Scan.annotated(annotated).pkg(packages).getClasses();
	}

}
