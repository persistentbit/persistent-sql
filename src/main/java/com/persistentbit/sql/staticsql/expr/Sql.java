package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PByteList;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.staticsql.ENumberGroup;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User: petermuys
 * Date: 15/10/16
 * Time: 13:17
 */
public class Sql{

	static public <N extends Number> ExprValueNumber<N> val(N number) {
		return new ExprValueNumber<>(number.getClass(), number);
	}

	static public ExprValueNumber<Short> val(Short number) {
		return new ExprValueNumber<>(Short.class, number);
	}

	static public ExprValueNumber<Integer> val(Integer number) {
		return new ExprValueNumber<>(Integer.class, number);
	}

	static public ExprValueNumber<Long> val(Long number) {
		return new ExprValueNumber<>(Long.class, number);
	}

	static public ExprValueNumber<Float> val(Float number) {
		return new ExprValueNumber<>(Float.class, number);
	}

	static public ExprValueNumber<Double> val(Double number) {
		return new ExprValueNumber<>(Double.class, number);
	}

	static public ExprValueBinary val(PByteList bin) { return new ExprValueBinary(bin); }

	static public ExprValueBinary val(byte[] binData) { return new ExprValueBinary(PByteList.from(binData)); }


	static public ETypeString val(String value) {
		return new ExprValueString(value);
	}

	static public ETypeBoolean val(Boolean value) { return new ExprValueBoolean(value);}

	static public ETypeDate val(LocalDate date) {
		return new ExprValueDate(date);
	}

	static public ETypeDateTime val(LocalDateTime dateTime) {
		return new ExprValueDateTime(dateTime);
	}

	static public ETypeBoolean exists(ETypeList<?> list) {
		return new ExprExists(list);
	}

	static public EBooleanGroup group(ETypeBoolean b) {
		return new EBooleanGroup(b);
	}

	static public <N extends Number> ENumberGroup<N> group(ETypeNumber<N> v) {
		return new ENumberGroup<>(v);
	}

	static public EStringGroup group(ETypeString v) {
		return new EStringGroup(v);
	}


	@SuppressWarnings("unchecked")
	static public <T extends Enum<T>> ETypeEnum<T> val(T value) {
		if(value == null) {
			throw new PersistSqlException("Need to know the class of the null enum: use Expr.valNullEnum(cls) instead.");
		}
		return new ExprValueEnum<T>(value, (Class<T>) value.getClass());
	}


	static public <T extends Enum<T>> ETypeEnum<T> valNullEnum(Class<T> value) {
		return new ExprValueEnum<>(null, value);
	}

}
