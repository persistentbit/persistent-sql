package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.sql.codegen.DbJavaGenException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 28/09/2016
 */
public interface Expr<S>{

    static <N extends Number> ExprConstNumber<N> val(N number){
        return new ExprConstNumber<N>(number);
    }
    static ETypeString  val(String value){
        return new ExprConstString(value);
    }

    static ETypeBoolean val(Boolean value) { return new ExprBoolean(value);}

    static ETypeDate    val(LocalDate date){
        return new ExprDate(date);
    }

    static ETypeDateTime val(LocalDateTime dateTime){
        return new ExprDateTime(dateTime);
    }

    static <T extends Enum<?>> ETypeEnum<T> val(T value){
        if(value == null){
            throw new DbJavaGenException("Need to know the class of the null enum: use Expr.valNullEnum(cls) instead.");
        }
        return new ExprEnum<>(value,value.getClass());
    }

    static <T extends Enum<T>> ETypeEnum<T> valNullEnum(Class<T> value){
        return new ExprEnum<>(null,value);
    }

    default <R> EMapper<S,R> map(Function<S,R> mapper){
        return new EMapper<>(this,mapper);
    }


    default <T2> ETuple2<S,T2> mergeWith(Expr<T2> expr2){
        return new ETuple2<>(this,expr2);
    }
    default <T2,T3> ETuple3<S,T2,T3> mergeWith(Expr<T2> v2, Expr<T3> v3){
        return new ETuple3<>(this,v2,v3);
    }
    default <T2,T3,T4> ETuple4<S,T2,T3,T4> mergeWith(Expr<T2> v2, Expr<T3> v3, Expr<T4> v4){
        return new ETuple4<>(this,v2,v3,v4);
    }
    default <T2,T3,T4,T5> ETuple5<S,T2,T3,T4,T5> mergeWith(
            Expr<T2> v2, Expr<T3> v3, Expr<T4> v4, Expr<T5> v5){
        return new ETuple5<>(this,v2,v3,v4,v5);
    }
    default <T2,T3,T4,T5,T6> ETuple6<S,T2,T3,T4,T5,T6> mergeWith(
            Expr<T2> v2, Expr<T3> v3, Expr<T4> v4, Expr<T5> v5, Expr<T6> v6){
        return new ETuple6<>(this,v2,v3,v4,v5,v6);
    }
    default <T2,T3,T4,T5,T6,T7> ETuple7<S,T2,T3,T4,T5,T6,T7> mergeWith(
            Expr<T2> v2, Expr<T3> v3, Expr<T4> v4, Expr<T5> v5, Expr<T6> v6, Expr<T7> v7){
        return new ETuple7<>(this,v2,v3,v4,v5,v6,v7);
    }
    <R> R accept(ExprVisitor<R> visitor);

}
