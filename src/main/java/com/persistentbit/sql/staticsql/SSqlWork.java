package com.persistentbit.sql.staticsql;

import com.persistentbit.core.OK;
import com.persistentbit.core.function.ThrowingFunction;
import com.persistentbit.core.logging.FunctionLogging;
import com.persistentbit.core.logging.entries.LogContext;
import com.persistentbit.core.logging.entries.LogEntryFunction;
import com.persistentbit.core.result.Result;
import com.persistentbit.sql.dbwork.DbTransManager;
import com.persistentbit.sql.dbwork.DbWork;

/**
 * TODOC
 *
 * @author petermuys
 * @since 13/01/17
 */
@FunctionalInterface
public interface SSqlWork<R>{


	Result<R> execute(DbContext dbc, DbTransManager tm) throws Exception;

	default <T> SSqlWork<T> map(ThrowingFunction<R, T, Exception> f) {
		return SSqlWork.function().code(l -> (dbc, tm) -> {
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

	default <T> SSqlWork<T> flatMap(ThrowingFunction<R, Result<T>, Exception> mapper) {
		return (dbc, tm) -> this.execute(dbc, tm).flatMap(r -> {
			try {
				return mapper.apply(r);
			} catch(Exception e) {
				return Result.failure(e);
			}
		});
	}

	default <T> SSqlWork<T> andThen(ThrowingFunction<Result<R>, SSqlWork<T>, Exception> after) {
		return (dbc, tm) -> {
			Result<R> thisResult = this.execute(dbc, tm);
			if(thisResult.isError()) {
				return thisResult.map(v -> null);
			}
			Result<T> afterResult = after.apply(thisResult).execute(dbc, tm);
			return afterResult.mapLog(l -> thisResult.getLog().append(l));
		};
	}

	static SSqlWork<OK> sequence(Iterable<SSqlWork<OK>> sequence) {
		return SSqlWork.function().code(log -> (dbc, tm) -> {
			for(SSqlWork<OK> w : sequence) {
				Result<OK> itemOK = w.execute(dbc, tm);
				if(itemOK.isError()) {
					return itemOK;
				}
				log.add(itemOK);
			}
			return OK.result;
		});
	}

	default DbWork<R> asDbWork(DbContext dbc) {
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
		public interface SSqlWorkWithLogging<R>{

			SSqlWork<R> create(SSqlWork.FLogging log) throws Exception;
		}

		@SuppressWarnings("unchecked")
		public <R> SSqlWork<R> code(SSqlWorkWithLogging<R> code) {
			return (dbc, tm) ->
				code
					.create(this)
					.execute(dbc, tm).mapLog(l -> getLog().append(l))
				;
		}

	}

}
