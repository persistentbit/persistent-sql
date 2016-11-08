package com.persistentbit.sql.substemagen;

import com.persistentbit.substema.compiler.values.RClass;
import com.persistentbit.substema.compiler.values.RTypeSig;

import java.sql.Types;
import java.util.Optional;

import static com.persistentbit.substema.compiler.SubstemaUtils.*;

/**
 * @author Peter Muys
 * @since 18/08/2015
 */
public enum SqlType{
	sBit(Types.BIT, booleanRClass),
	sTinyInt(Types.TINYINT, shortRClass),
	sSmallInt(Types.SMALLINT, shortRClass),
	sInteger(Types.INTEGER, integerRClass),
	sBigint(Types.BIGINT, longRClass),
	sFloat(Types.FLOAT, floatRClass),
	sReal(Types.REAL, doubleRClass),
	sDouble(Types.DOUBLE, doubleRClass),
	sNumeric(Types.NUMERIC, doubleRClass),
	sDecimal(Types.DECIMAL, doubleRClass),
	sChar(Types.CHAR, stringRClass),
	sVarChar(Types.VARCHAR, stringRClass),
	sLongVarChar(Types.LONGVARCHAR, stringRClass),
	sDate(Types.DATE, dateRClass),
	sTime(Types.TIME),
	sTimestamp(Types.TIMESTAMP, dateTimeRClass),
	sBinary(Types.BINARY, binaryRClass),
	sVarBinary(Types.VARBINARY, binaryRClass),
	sLongVarBinary(Types.LONGVARBINARY, binaryRClass),
	sBlob(Types.BLOB, binaryRClass),
	sClob(Types.CLOB, stringRClass),
	sNClob(Types.NCLOB, binaryRClass),
	sBoolean(Types.BOOLEAN, booleanRClass),
	sNChar(Types.NCHAR, stringRClass),
	sNVarChar(Types.NVARCHAR, stringRClass),
	sLongNVarChar(Types.LONGNVARCHAR, stringRClass),
	sSqlXml(Types.SQLXML, stringRClass),
	sJavaObject(Types.JAVA_OBJECT);

	final int      javaSqlType;
	final RTypeSig rTypeSig;

	SqlType(int javaSqlType, RClass rClass) {
		this(javaSqlType, rClass == null ? null : new RTypeSig(rClass));
	}

	SqlType(int javaSqlType, RTypeSig typeSig) {
		this.javaSqlType = javaSqlType;
		this.rTypeSig = typeSig;
	}

	SqlType(int javaSqlType) {
		this(javaSqlType, (RTypeSig) null);
	}

	public static SqlType fromJavaSqlType(int data_type) {
		for(SqlType s : values()) {
			if(s.javaSqlType == data_type) {
				return s;
			}
		}
		throw new RuntimeException("Unknown: " + data_type);
	}

	public Optional<RTypeSig> getTypeSig() {
		return Optional.ofNullable(rTypeSig);
	}
}
