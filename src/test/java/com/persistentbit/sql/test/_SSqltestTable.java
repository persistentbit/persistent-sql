// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-15T11:05:17.131

package com.persistentbit.sql.test;

import com.persistentbit.core.tuples.Tuple2;
import java.time.LocalDateTime;
import com.persistentbit.sql.staticsql.expr.Expr;
import com.persistentbit.sql.staticsql.expr.ExprPropertyNumber;
import com.persistentbit.sql.staticsql.expr.ExprToSqlContext;
import com.persistentbit.core.collections.PList;
import com.persistentbit.sql.staticsql.expr.ETypeString;
import com.persistentbit.sql.staticsql.RowReader;
import com.persistentbit.sql.staticsql.expr.Sql;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.Insert;
import com.persistentbit.sql.staticsql.expr.ETypeNumber;
import java.util.Optional;
import com.persistentbit.sql.staticsql.expr.ExprPropertyDateTime;
import com.persistentbit.sql.staticsql.DbWork;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.Delete;
import com.persistentbit.sql.staticsql.expr.ETypePropertyParent;
import com.persistentbit.sql.staticsql.Update;
import com.persistentbit.core.result.Result;
import com.persistentbit.sql.staticsql.expr.ExprPropertyString;
import com.persistentbit.sql.staticsql.Query;
import com.persistentbit.sql.staticsql.expr.ETypeDateTime;

/**
 * 
 * This immutable value class contains the data for a record in the table 'SQLTEST_TABLE'.<br>
 * Generated from the database on 2017-01-15T11:04:40.74<br>
 * 
 */
public class _SSqltestTable implements ETypeObject<SSqltestTable> {
	private final ETypePropertyParent __parent;
	public _SSqltestTable(ETypePropertyParent parent) {
		this.__parent = parent;
	}
	
	public _SSqltestTable() {
		this(null);
	}
	
	@Override
	public Optional<ETypePropertyParent> getParent() { return Optional.ofNullable(this.__parent); }
	
	@Override
	public String _getTableName() {
		return "SQLTEST_TABLE";
	}
	
	@Override
	public String toString() { return _getTableName(); }
	
	
	@Override
	public String _fullColumnName(ExprToSqlContext context) { return __parent == null ? "" : __parent._fullColumnName(context); }
	public ETypeNumber<Integer> id = new ExprPropertyNumber<>(Integer.class,this,"id", "ID");
	public ETypeDateTime createdDate = new ExprPropertyDateTime(this,"createdDate", "CREATED_DATE");
	public ETypeString moduleName = new ExprPropertyString(this,"moduleName", "MODULE_NAME");
	public ETypeString className = new ExprPropertyString(this,"className", "CLASS_NAME");
	public ETypeString methodName = new ExprPropertyString(this,"methodName", "METHOD_NAME");
	
	@Override
	public PList<Tuple2<String,Expr<?>>> _all() {
		return PList.val(Tuple2.of("id",id), Tuple2.of("createdDate",createdDate), Tuple2.of("moduleName",moduleName), Tuple2.of("className",className), Tuple2.of("methodName",methodName));
	}
	
	public PList<Expr<?>> _asExprValues(SSqltestTable v) {
		return _SSqltestTable.asValues(v);
	}
	static public PList<Expr<?>> asValues(SSqltestTable v) {
		PList<Expr<?>> r = PList.empty();
		r = r.plus(Sql.val(v.getId()));
		r = r.plus(Sql.val(v.getCreatedDate()));
		r = r.plus(Sql.val(v.getModuleName()));
		r = r.plus(Sql.val(v.getClassName()));
		r = r.plus(Sql.val(v.getMethodName()));
		return r;
	}
	
	public PList<Expr<?>> _expand() {
		PList<Expr<?>> res = PList.empty();
		res = res.plusAll(id._expand());
		res = res.plusAll(createdDate._expand());
		res = res.plusAll(moduleName._expand());
		res = res.plusAll(className._expand());
		res = res.plusAll(methodName._expand());
		return res;
	}
	public SSqltestTable read(RowReader _rowReader, ExprRowReaderCache _cache) {
		Integer id = this.id.read(_rowReader,_cache);
		LocalDateTime createdDate = this.createdDate.read(_rowReader,_cache);
		String moduleName = this.moduleName.read(_rowReader,_cache);
		String className = this.className.read(_rowReader,_cache);
		String methodName = this.methodName.read(_rowReader,_cache);
		if(id==null || createdDate==null || moduleName==null || className==null || methodName==null) { return null; }
		return _cache.updatedFromCache(SSqltestTable.build(b-> b
			.setId(id)
			.setCreatedDate(createdDate)
			.setModuleName(moduleName)
			.setClassName(className)
			.setMethodName(methodName)
		));
	}
	public Optional<Expr<?>> _getAutoGenKey() {
		return Optional.of(id);
	}
	public SSqltestTable _setAutoGenKey(SSqltestTable object, Object value) {
		return object.withId((Integer) value);
	}
	public DbWork<SSqltestTable> insert(SSqltestTable newRow) {
		return Insert.into(this,val(newRow)).withGeneratedKeys(_getAutoGenKey().get())
			.map(key -> _setAutoGenKey(newRow,key));
	}
	public DbWork<SSqltestTable> selectById(Integer id) {
		return Query.from(this).where(this.id.eq(id)).selection(this).flatMap(l -> Result.fromOpt(l.headOpt()));
	}
	public DbWork<Integer> deleteById(Integer id) {
		return new Delete(this).where(this.id.eq(id));
	}
	public DbWork<SSqltestTable> update(SSqltestTable _row) {
		return new Update(this)
		.set(createdDate, Sql.val(_row.getCreatedDate()))
		.set(moduleName, Sql.val(_row.getModuleName()))
		.set(className, Sql.val(_row.getClassName()))
		.set(methodName, Sql.val(_row.getMethodName()))
		.where(this.id.eq(_row.getId()))
		.flatMap(count -> count == 0
			? Result.empty()
			: count == 1 ? Result.success(_row) : Result.failure("More than one record update: " + count)
		);
	}
}
