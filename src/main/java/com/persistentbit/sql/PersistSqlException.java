package com.persistentbit.sql;

/**
 * @author Peter Muys
 * @since 13/07/2016
 */
public class PersistSqlException extends RuntimeException{

	public PersistSqlException() {
		super();
	}

	public PersistSqlException(String message) {
		super(message);
	}

	public PersistSqlException(String message, Throwable cause) {
		super(message, cause);
	}

	public PersistSqlException(Throwable cause) {
		super(cause);
	}

	protected PersistSqlException(String message, Throwable cause, boolean enableSuppression,
								  boolean writableStackTrace
	) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
