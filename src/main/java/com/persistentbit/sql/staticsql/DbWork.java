package com.persistentbit.sql.staticsql;

import com.persistentbit.core.OK;
import com.persistentbit.core.function.ThrowingFunction;
import com.persistentbit.core.logging.FunctionLogging;
import com.persistentbit.core.logging.entries.LogContext;
import com.persistentbit.core.logging.entries.LogEntryFunction;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.sqlwork.DbTransManager;
import com.persistentbit.sql.sqlwork.SqlWork;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A DBWork lambda is a piece of code that accesses a Database.<br>
 * To execute the code, a {@link DbContext} and a {@link DbTransManager}
 * @author petermuys
 * @since 13/01/17
 */
@FunctionalInterface
public interface DbWork<R>{


	Result<R> execute(DbContext dbc, DbTransManager tm) throws Exception;

	default Result<R> run(DbWorkRunner runner) {
		return runner.run(this);
	}

	default <T> DbWork<T> map(ThrowingFunction<R, T, Exception> f) {
		return DbWork.function().code(l -> (dbc, tm) -> {
			try {
				Result<R> thisResult = this.execute(dbc, tm);
				if(thisResult.isError()) {
					return thisResult.map(v -> null);
				}
				R thisValue = thisResult.orElseThrow();
				return Result.success(f.apply(thisValue));
			} catch(Exception e) {
				return Result.failure(e);
			}
		});
	}

	default <T> DbWork<T> flatMap(ThrowingFunction<R, Result<T>, Exception> mapper) {
		return DbWork.function().code(l -> (dbc, tm) ->
			this.execute(dbc, tm)
				.flatMap(r -> {
					try {
						return mapper.apply(r);
					} catch(Exception e) {
						return Result.<T>failure(e);
					}
				})
		);
	}

	default DbWork<R> verify(Predicate<R> condition, String message) {
		return this.flatMap(r ->
								condition.test(r)
									? Result.success(r)
									: Result.failure("Verification of " + r + " failed:" + message)
		);
	}

	default DbWork<OK> verifyToOK(Predicate<R> condition, String message) {
		return this.flatMap(r ->
								condition.test(r)
									? OK.result
									: Result.failure("Verification of " + r + " failed:" + message)
		);
	}

	default <T> DbWork<T> andThen(ThrowingFunction<Result<R>, DbWork<T>, Exception> after) {
		return (dbc, tm) -> {
			Result<R> thisResult = this.execute(dbc, tm);
			if(thisResult.isError()) {
				return thisResult.map(v -> null);//Convert the failure from <R> to <T>
			}
			Result<T> afterResult = after.apply(thisResult).execute(dbc, tm);
			return afterResult.mapLog(l -> thisResult.getLog().append(l));
		};
	}

	default <OTHER> DbWork<Tuple2<R, OTHER>> combine(Function<R, DbWork<OTHER>> other) {
		return (dbc, tm) -> {
			Result<R> resR = execute(dbc, tm);
			if(resR.isPresent() == false) {
				return resR.map(v -> null); //Map error
			}
			R r = resR.orElseThrow();
			return other.apply(r).execute(dbc, tm)
						.map(o -> Tuple2.of(r, o));
		};
	}


	default <T> DbWork<T> andThenOnSuccess(ThrowingFunction<R, DbWork<T>, Exception> after) {
		return (dbc, tm) -> {
			Result<R> thisResult = this.execute(dbc, tm);
			if(thisResult.isPresent() == false) {
				return thisResult.map(v -> null);//Convert the failure or empty from <R> to <T>
			}
			Result<T> afterResult = after.apply(thisResult.orElseThrow()).execute(dbc, tm);
			return afterResult.mapLog(l -> thisResult.getLog().append(l));
		};
	}

	static DbWork<OK> sequence(Iterable<DbWork<OK>> sequence) {
		return DbWork.function().code(log -> (dbc, tm) -> {
			for(DbWork<OK> w : sequence) {
				Result<OK> itemOK = w.execute(dbc, tm);
				if(itemOK.isError()) {
					return itemOK;
				}
				log.add(itemOK);
			}
			return OK.result;
		});
	}

	default SqlWork<R> asSqlWork(DbContext dbc) {
		return tm -> execute(dbc, tm);
	}


	static FLogging function() {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		LogEntryFunction  fe  = LogEntryFunction.of(new LogContext(ste));
		return new FLogging(fe);
	}

	static FLogging function(Object... params) {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
		LogEntryFunction  fe  = LogEntryFunction.of(new LogContext(ste));
		FLogging          res = new FLogging(fe);
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

			DbWork<R> create(DbWork.FLogging log) throws Exception;
		}

		@SuppressWarnings("unchecked")
		public <R> DbWork<R> code(DbWorkWithLogging<R> code) {
			return (dbc, tm) ->
				code
					.create(this)
					.execute(dbc, tm).mapLog(l -> getLog().append(l))
				;
		}

	}

}
