package com.persistentbit.sql.statement.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Peter Muys
 * @since 19/07/2016
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public  @interface DbIgnore {
}
