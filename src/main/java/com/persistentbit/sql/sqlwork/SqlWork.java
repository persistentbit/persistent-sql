package com.persistentbit.sql.sqlwork;

import com.persistentbit.core.function.ThrowingFunction;
import com.persistentbit.core.logging.FunctionLogging;
import com.persistentbit.core.logging.entries.LogContext;
import com.persistentbit.core.logging.entries.LogEntryFunction;
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
public interface SqlWork<R>{

	Result<R> execute(DbTransManager tm) throws Exception;

	default <T> SqlWork<T> flatMap(ThrowingFunction<R, SqlWork<T>, Exception> f) {
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

	default <T> SqlWork<T> map(Function<R, T> f) {
		return tm -> this.execute(tm).map(f);
	}

	default <T> SqlWork<Tuple2<R, T>> combine(SqlWork<T> other) {
		return tm -> this.execute(tm).combine(other.execute(tm));
	}


	static SqlWork.FLogging function() {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		LogEntryFunction  fe  = LogEntryFunction.of(new LogContext(ste));
		return new SqlWork.FLogging(fe);
	}

	static SqlWork.FLogging function(Object... params) {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		LogEntryFunction  fe  = LogEntryFunction.of(new LogContext(ste));
		SqlWork.FLogging  res = new SqlWork.FLogging(fe);
		res.params(params);
		return res;
	}


	class FLogging extends FunctionLogging{

		public FLogging(LogEntryFunction lef, int stackEntryIndex) {
			super(lef, stackEntryIndex);
		}

		public FLogging(LogEntryFunction lef) {
			this(lef, 2);
		}

		@FunctionalInterface
		public interface DbWorkWithLogging<R>{

			SqlWork<R> create(SqlWork.FLogging log) throws Exception;
		}

		@SuppressWarnings("unchecked")
		public <R> SqlWork<R> code(SqlWork.FLogging.DbWorkWithLogging<R> code) {
			return tm ->
				code
					.create(this)
					.execute(tm).mapLog(l -> getLog().append(l))
				;
		}

	}
}
