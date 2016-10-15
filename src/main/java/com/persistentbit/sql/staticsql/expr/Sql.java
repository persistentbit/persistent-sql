package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.staticsql.ENumberGroup;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * User: petermuys
 * Date: 15/10/16
 * Time: 13:17
 */
public class Sql {
    static  public <N extends Number> ExprConstNumber<N> val(N number){
        return new ExprConstNumber<>(number.getClass(),number);
    }
    static  public ExprConstNumber<Short> val(Short number){
        return new ExprConstNumber<>(Short.class,number);
    }

    static  public ExprConstNumber<Integer> val(Integer number){
        return new ExprConstNumber<>(Integer.class,number);
    }

    static  public ExprConstNumber<Long> val(Long number){
        return new ExprConstNumber<>(Long.class,number);
    }

    static  public ExprConstNumber<Float> val(Float number){
        return new ExprConstNumber<>(Float.class,number);
    }

    static  public ExprConstNumber<Double> val(Double number){
        return new ExprConstNumber<>(Double.class,number);
    }



    static public ETypeString  val(String value){
        return new ExprConstString(value);
    }

    static public ETypeBoolean val(Boolean value) { return new ExprBoolean(value);}

    static public ETypeDate    val(LocalDate date){
        return new ExprDate(date);
    }

    static public ETypeDateTime val(LocalDateTime dateTime){
        return new ExprDateTime(dateTime);
    }

    static public ETypeBoolean exists(ETypeList<?> list){
        return new ExprExists(list);
    }

    static public EBooleanGroup    group(ETypeBoolean b){
        return new EBooleanGroup(b);
    }
    static public <N extends Number> ENumberGroup<N> group(ETypeNumber<N> v){
        return new ENumberGroup<>(v);
    }
    static public EStringGroup group(ETypeString v){
        return new EStringGroup(v);
    }

    static public <T extends Enum<T>> ETypeEnum<T> val(T value){
        if(value == null){
            throw new PersistSqlException("Need to know the class of the null enum: use Expr.valNullEnum(cls) instead.");
        }
        return new ExprEnum<T>(value,value.getClass());
    }


    static public <T extends Enum<T>> ETypeEnum<T> valNullEnum(Class<T> value){
        return new ExprEnum<>(null,value);
    }

}
