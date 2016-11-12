package com.persistentbit.sql.staticsql;

import com.persistentbit.sql.staticsql.expr.Expr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TODOC
 * @author Peter Muys
 * @since 3/10/16
 */
public class ExprRowReader implements ExprRowReaderCache{

	private final Map<Object, Object> cache         = new HashMap<>();
	private final Set<Object>         usedFromCache = new HashSet<>();

	@Override
	public Object updatedFromCache(Object value) {
		if(value == null) {
			return null;
		}
		Object cached = cache.get(value);
		if(cached != null) {
			//System.out.println("Cached: " + cached);
			usedFromCache.add(cached);
			return cached;
		}
		cache.put(value, value);
		return value;
	}


	public <T> T read(Expr<T> expr, RowReader reader) {
		return expr.read(reader, this);
	}
}
