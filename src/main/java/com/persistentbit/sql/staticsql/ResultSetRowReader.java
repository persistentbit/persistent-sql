package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PByteList;
import com.persistentbit.core.exceptions.RtSqlException;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * RowReader implementation that
 * reads from a {@link ResultSet}
 *
 * @author Peter Muys
 * @since 3/10/16
 */
public class ResultSetRowReader implements RowReader{

	private final ResultSet rs;
	private int index = 1;

	public ResultSetRowReader(ResultSet rs) {
		this.rs = rs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T readNext(Class<T> cls) {
		try {
			if(cls.equals(LocalDate.class)) {
				Date dt = rs.getObject(index++, Date.class);
				if(dt == null) { return null; }

				return (T) dt.toLocalDate();
			}
			else if(cls.equals(LocalDateTime.class)) {
				Timestamp ts = rs.getObject(index++, Timestamp.class);
				if(ts == null) { return null; }
				return (T) ts.toLocalDateTime();
			}
			else if(cls.equals(PByteList.class)) {
				byte[] bytes = rs.getBytes(index++);
				return (T) (bytes == null ? null : PByteList.from(bytes));
			}

			return rs.getObject(index++, cls);
		} catch(SQLException e) {
			throw new RtSqlException("SQL error while reading column " + index + " from result set", e);
		}

	}


	public ResultSetRowReader nextRow() {
		index = 1;
		return this;
	}
}
