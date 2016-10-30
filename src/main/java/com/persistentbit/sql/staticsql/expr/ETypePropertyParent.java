package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.utils.ToDo;

import java.util.Optional;

/**
 * User: petermuys
 * Date: 14/10/16
 * Time: 18:11
 */
public interface ETypePropertyParent<T> extends Expr<T>{

	String _asParentName(ExprToSqlContext context, String propertyName);/*{
		if(getParent().isPresent()){
            return getParent().get()._asParentName(context) + "_" + getInstanceName();
        }
        String name = context.uniqueInstanceName(this,getInstanceName());
        return name + ".";



    }*/

	Optional<ETypePropertyParent> getParent();

	String getInstanceName();

	default ETypeObject<T> withNewParent(ETypePropertyParent newParent) {
		throw new ToDo();
	}
}
