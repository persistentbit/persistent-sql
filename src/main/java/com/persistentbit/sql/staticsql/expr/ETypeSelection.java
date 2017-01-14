package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.DbWork;
import com.persistentbit.sql.staticsql.Query;

import java.util.Optional;

/**
 * Created by petermuys on 14/10/16.
 */
public interface ETypeSelection<T> extends ETypeObject<T>, ETypeList<T>, DbWork<PList<T>>{

	@Override
	default Optional<ETypePropertyParent> getParent() {
		return Optional.empty();
	}

	@Override
	default String _getTableName() {
		return "selection";
	}


	Query getQuery();


	PList<BaseSelection<?>.SelectionProperty<?>> selections();


}
