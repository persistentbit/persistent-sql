// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-15T11:05:17.119

package com.persistentbit.sql.test;

import com.persistentbit.core.tuples.Tuple2;
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
import com.persistentbit.sql.staticsql.DbWork;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.Delete;
import com.persistentbit.sql.staticsql.expr.ETypePropertyParent;
import com.persistentbit.sql.staticsql.Update;
import com.persistentbit.core.result.Result;
import com.persistentbit.sql.staticsql.expr.ExprPropertyString;
import com.persistentbit.sql.staticsql.Query;

/**
 * 
 * This immutable value class contains the data for a record in the table 'DB_UPDATE_TEST'.<br>
 * Generated from the database on 2017-01-15T11:04:40.721<br>
 * 
 */
public class _SDbUpdateTest implements ETypeObject<SDbUpdateTest> {
	private final ETypePropertyParent __parent;
	public _SDbUpdateTest(ETypePropertyParent parent) {
		this.__parent = parent;
	}
	
	public _SDbUpdateTest() {
		this(null);
	}
	
	@Override
	public Optional<ETypePropertyParent> getParent() { return Optional.ofNullable(this.__parent); }
	
	@Override
	public String _getTableName() {
		return "DB_UPDATE_TEST";
	}
	
	@Override
	public String toString() { return _getTableName(); }
	
	
	@Override
	public String _fullColumnName(ExprToSqlContext context) { return __parent == null ? "" : __parent._fullColumnName(context); }
	public ETypeNumber<Integer> id = new ExprPropertyNumber<>(Integer.class,this,"id", "ID");
	public ETypeString name = new ExprPropertyString(this,"name", "NAME");
	
	@Override
	public PList<Tuple2<String,Expr<?>>> _all() {
		return PList.val(Tuple2.of("id",id), Tuple2.of("name",name));
	}
	
	public PList<Expr<?>> _asExprValues(SDbUpdateTest v) {
		return _SDbUpdateTest.asValues(v);
	}
	static public PList<Expr<?>> asValues(SDbUpdateTest v) {
		PList<Expr<?>> r = PList.empty();
		r = r.plus(Sql.val(v.getId()));
		r = r.plus(Sql.val(v.getName().orElse(null)));
		return r;
	}
	
	public PList<Expr<?>> _expand() {
		PList<Expr<?>> res = PList.empty();
		res = res.plusAll(id._expand());
		res = res.plusAll(name._expand());
		return res;
	}
	public SDbUpdateTest read(RowReader _rowReader, ExprRowReaderCache _cache) {
		Integer id = this.id.read(_rowReader,_cache);
		String name = this.name.read(_rowReader,_cache);
		if(id==null) { return null; }
		return _cache.updatedFromCache(SDbUpdateTest.build(b-> b
			.setId(id)
			.setName(name)
		));
	}
	public Optional<Expr<?>> _getAutoGenKey() {
		return Optional.of(id);
	}
	public SDbUpdateTest _setAutoGenKey(SDbUpdateTest object, Object value) {
		return object.withId((Integer) value);
	}
	public DbWork<SDbUpdateTest> insert(SDbUpdateTest newRow) {
		return Insert.into(this,val(newRow)).withGeneratedKeys(_getAutoGenKey().get())
			.map(key -> _setAutoGenKey(newRow,key));
	}
	public DbWork<SDbUpdateTest> selectById(Integer id) {
		return Query.from(this).where(this.id.eq(id)).selection(this).flatMap(l -> Result.fromOpt(l.headOpt()));
	}
	public DbWork<Integer> deleteById(Integer id) {
		return new Delete(this).where(this.id.eq(id));
	}
	public DbWork<SDbUpdateTest> update(SDbUpdateTest _row) {
		return new Update(this)
		.set(name, Sql.val(_row.getName().orElse(null)))
		.where(this.id.eq(_row.getId()))
		.flatMap(count -> count == 0
			? Result.empty()
			: count == 1 ? Result.success(_row) : Result.failure("More than one record update: " + count)
		);
	}
}
