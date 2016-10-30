package com.persistentbit.sql.staticsql.codegen;

/**
 * @author Peter Muys
 * @since 5/10/2016
 */
public class DbJavaGenException extends RuntimeException{

	public DbJavaGenException() {
	}

	public DbJavaGenException(String message) {
		super(message);
	}

	public DbJavaGenException(String message, Throwable cause) {
		super(message, cause);
	}

	public DbJavaGenException(Throwable cause) {
		super(cause);
	}

	public DbJavaGenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
