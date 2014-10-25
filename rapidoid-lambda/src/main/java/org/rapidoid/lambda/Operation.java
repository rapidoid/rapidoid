package org.rapidoid.lambda;

public interface Operation<T> {

	void execute(T obj) throws Exception;

}
