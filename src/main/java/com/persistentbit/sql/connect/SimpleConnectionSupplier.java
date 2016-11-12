package com.persistentbit.sql.connect;

import com.persistentbit.sql.PersistSqlException;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * A Simple {@link Connection} supplier that uses a jdbc driver to create new Connections.
 *
 * @author Peter Muys
 * @since 13/07/2016
 */
public class SimpleConnectionSupplier implements Supplier<Connection>{

	private final String url;
	private final String userName;
	private final String passWord;

	public SimpleConnectionSupplier(String driverClass, String url) {
		this(driverClass, url, null, null);
	}

	public SimpleConnectionSupplier(String driverClass, String url, String userName, String password) {
		try {
			Driver driver = (Driver) Class.forName(driverClass).newInstance();
			DriverManager.registerDriver(driver);
		} catch(InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			throw new PersistSqlException(e);
		}
		this.url = url;
		this.passWord = password;
		this.userName = userName;
	}

	@Override
	public Connection get() {
		try {
			Connection c;
			if(userName != null) {
				c = DriverManager.getConnection(url, userName, passWord);
			}
			else {
				c = DriverManager.getConnection(url);
			}
			c.setAutoCommit(false);
			return c;

		} catch(SQLException e) {
			throw new PersistSqlException(e);
		}
	}
}
