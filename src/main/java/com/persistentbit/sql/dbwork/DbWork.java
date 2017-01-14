package com.persistentbit.sql.dbwork;

import com.persistentbit.core.function.ThrowingFunction;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;

import java.util.function.Function;

/**
 * TODOC
 *
 * @author petermuys
 * @since 12/01/17
 */
@FunctionalInterface
public interface DbWork<R>{

	Result<R> execute(DbTransManager tm) throws Exception;

	default <T> DbWork<T> flatMap(ThrowingFunction<R, DbWork<T>, Exception> f) {
		return tm -> {
			Result<R> thisResult;
			try {
				thisResult = this.execute(tm);
			} catch(Exception e) {
				return Result.failure(e);
			}
			if(thisResult.isError()) {
				return thisResult.map(v -> null);
			}
			R thisValue = thisResult.orElseThrow();
			return f.apply(thisValue).execute(tm);
		};
	}

	default <T> DbWork<T> map(Function<R, T> f) {
		return tm -> this.execute(tm).map(f);
	}

	default <T> DbWork<Tuple2<R, T>> combine(DbWork<T> other) {
		return tm -> this.execute(tm).combine(other.execute(tm));
	}

}
