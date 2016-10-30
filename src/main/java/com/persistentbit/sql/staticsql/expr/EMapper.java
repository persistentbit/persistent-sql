package com.persistentbit.sql.staticsql.expr;

import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;

import java.util.function.Function;

/**
 * Created by petermuys on 2/10/16.
 */
public class EMapper<T, R> implements Expr<R>{

	private Expr<T>        expr;
	private Function<T, R> mapper;

	public EMapper(Expr<T> expr, Function<T, R> mapper) {
		this.expr = expr;
		this.mapper = mapper;
	}


	public Expr<T> getExpr() {
		return expr;
	}

	public Function<T, R> getMapper() {
		return mapper;
	}

	@Override
	public R read(RowReader _rowReader, ExprRowReaderCache _cache) {
		/*T value = expr.read(_rowReader,_cache);
        if(_cache.contains(value) == false){
            cache.remove(value);
        }
        value = updatedFromCache(value);
        return mapper.getMapper().apply(value);*/

		//TODO look to remove cached unmapped value if this is
		//The first time it is used

		return _cache.updatedFromCache(mapper.apply(expr.read(_rowReader, _cache)));
	}

	@Override
	public String _toSql(ExprToSqlContext context) {
		return expr._toSql(context);
	}

	@Override
	public PList<Expr<?>> _expand() {
		return expr._expand();
	}
}
