package com.persistentbit.sql.experiments;

import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;

import java.sql.SQLException;
import java.util.function.Function;

/**
 * TODOC
 *
 * @author petermuys
 * @since 12/01/17
 */
@FunctionalInterface
public interface DbWork<R>{

	Result<R> execute(DbTransManager tm) throws SQLException;

	default <T> DbWork<T> flatMap(Function<R, DbWork<T>> f) {
		return tm -> tm.run(this).flatMap(r -> tm.run(f.apply(r)));
	}

	default <T> DbWork<T> map(Function<R, T> f) {
		return tm -> tm.run(this).map(f);
	}

	default <T> DbWork<Tuple2<R, T>> combine(DbWork<T> other) {
		return tm -> tm.run(this).combine(tm.run(other));
	}

}
