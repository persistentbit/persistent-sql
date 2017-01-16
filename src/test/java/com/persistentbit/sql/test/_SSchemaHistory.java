// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.129

package com.persistentbit.sql.test;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.staticsql.DbWork;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Insert;
import com.persistentbit.sql.staticsql.RowReader;
import com.persistentbit.sql.staticsql.expr.*;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 
 * This immutable value class contains the data for a record in the table 'SCHEMA_HISTORY'.<br>
 * Generated from the database on 2017-01-15T11:05:17.022<br>
 * 
 */
public class _SSchemaHistory implements ETypeObject<SSchemaHistory> {
	private final ETypePropertyParent __parent;
	public _SSchemaHistory(ETypePropertyParent parent) {
		this.__parent = parent;
	}
	
	public _SSchemaHistory() {
		this(null);
	}
	
	@Override
	public Optional<ETypePropertyParent> getParent() { return Optional.ofNullable(this.__parent); }
	
	@Override
	public String _getTableName() {
		return "SCHEMA_HISTORY";
	}
	
	@Override
	public String toString() { return _getTableName(); }
	
	
	@Override
	public String _fullColumnName(ExprToSqlContext context) { return __parent == null ? "" : __parent._fullColumnName(context); }
	public ETypeDateTime createddate = new ExprPropertyDateTime(this,"createddate", "CREATEDDATE");
	public ETypeString packageName = new ExprPropertyString(this,"packageName", "PACKAGE_NAME");
	public ETypeString updateName = new ExprPropertyString(this,"updateName", "UPDATE_NAME");
	
	@Override
	public PList<Tuple2<String,Expr<?>>> _all() {
		return PList.val(Tuple2.of("createddate",createddate), Tuple2.of("packageName",packageName), Tuple2.of("updateName",updateName));
	}
	
	public PList<Expr<?>> _asExprValues(SSchemaHistory v) {
		return _SSchemaHistory.asValues(v);
	}
	static public PList<Expr<?>> asValues(SSchemaHistory v) {
		PList<Expr<?>> r = PList.empty();
		r = r.plus(Sql.val(v.getCreateddate()));
		r = r.plus(Sql.val(v.getPackageName()));
		r = r.plus(Sql.val(v.getUpdateName()));
		return r;
	}
	
	public PList<Expr<?>> _expand() {
		PList<Expr<?>> res = PList.empty();
		res = res.plusAll(createddate._expand());
		res = res.plusAll(packageName._expand());
		res = res.plusAll(updateName._expand());
		return res;
	}
	public SSchemaHistory read(RowReader _rowReader, ExprRowReaderCache _cache) {
		LocalDateTime createddate = this.createddate.read(_rowReader,_cache);
		String packageName = this.packageName.read(_rowReader,_cache);
		String updateName = this.updateName.read(_rowReader,_cache);
		if(createddate==null || packageName==null || updateName==null) { return null; }
		return _cache.updatedFromCache(SSchemaHistory.build(b-> b
			.setCreateddate(createddate)
			.setPackageName(packageName)
			.setUpdateName(updateName)
		));
	}
	public Optional<Expr<?>> _getAutoGenKey() {
		return Optional.empty();
	}
	public SSchemaHistory _setAutoGenKey(SSchemaHistory object, Object value) {
		throw new PersistSqlException(" There is no auto generated key for SSchemaHistory");
	}
	public DbWork<SSchemaHistory> insert(SSchemaHistory newRow) {
		return Insert.into(this,val(newRow)).map(count -> newRow);
	}
}
