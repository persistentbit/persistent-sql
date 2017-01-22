package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PByteList;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.Types.BLOB;

/**
 * Expression around a {@link PByteList} Binary value
 *
 * @author Peter Muys
 * @since 8/11/2016
 */
public class ExprValueBinary implements ETypeValue<PByteList>{

    private PByteList value;

    public ExprValueBinary(PByteList value) {
        this.value = value;
    }


    public PByteList getValue() {
        return value;
    }

    @Override
    public PByteList read(RowReader _rowReader, ExprRowReaderCache _cache) {
        return _rowReader.readNext(PByteList.class);
    }


    @Override
    public ETypeValue setValue(PByteList value) {
        this.value = value;
        return this;
    }

    @Override
    public void _setParam(PreparedStatement preparedStatement, int parameterIndex) throws SQLException {
        if(value == null) {
            preparedStatement.setNull(parameterIndex, BLOB);
        }
        else {
            preparedStatement.setBinaryStream(parameterIndex, value.getInputStream());
        }
    }

    @Override
    public String _literalValueToSql(ExprToSqlContext context) {
        return value == null ? "NULL" : context.getDbType().asLiteralBlob(value);
    }
}
