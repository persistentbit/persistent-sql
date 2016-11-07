package com.persistentbit.sql.connect;

import com.persistentbit.sql.PersistSqlException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * {@link Connection} supplier that used a {@link DataSource} to create new connections
 *
 * @author Peter Muys
 */
public class DatasourceConnectionSupplier implements Supplier<Connection>{

	private final DataSource dataSource;

	public DatasourceConnectionSupplier(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Connection get() {
		try {
			return dataSource.getConnection();
		} catch(SQLException e) {
			throw new PersistSqlException(e);
		}
	}
}
