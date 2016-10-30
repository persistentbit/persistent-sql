package com.persistentbit.sql.transactions;

/**
 * @author Peter Muys
 * @since 13/07/2016
 */
public class TransactionsException extends RuntimeException{

	public TransactionsException() {
	}

	public TransactionsException(String message) {
		super(message);
	}

	public TransactionsException(String message, Throwable cause) {
		super(message, cause);
	}
}
