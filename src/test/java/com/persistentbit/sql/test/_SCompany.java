// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.075

package com.persistentbit.sql.test;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.*;
import com.persistentbit.sql.staticsql.expr.*;

import java.util.Optional;

/**
 * 
 * This immutable value class contains the data for a record in the table 'COMPANY'.<br>
 * Generated from the database on 2017-01-15T11:05:16.988<br>
 * 
 */
public class _SCompany implements ETypeObject<SCompany> {
	private final ETypePropertyParent __parent;
	public _SCompany(ETypePropertyParent parent) {
		this.__parent = parent;
	}
	
	public _SCompany() {
		this(null);
	}
	
	@Override
	public Optional<ETypePropertyParent> getParent() { return Optional.ofNullable(this.__parent); }
	
	@Override
	public String _getTableName() {
		return "COMPANY";
	}
	
	@Override
	public String toString() { return _getTableName(); }
	
	
	@Override
	public String _fullColumnName(ExprToSqlContext context) { return __parent == null ? "" : __parent._fullColumnName(context); }
	public ETypeNumber<Integer> id    = new ExprPropertyNumber<>(Integer.class,this,"id", "ID");
	public _Address             adres = new _Address(new ExprProperty(Address.class, this, "adres", "ADRES"));
	
	@Override
	public PList<Tuple2<String,Expr<?>>> _all() {
		return PList.val(Tuple2.of("id", id), Tuple2.of("adres", adres));
	}
	
	public PList<Expr<?>> _asExprValues(SCompany v) {
		return _SCompany.asValues(v);
	}
	static public PList<Expr<?>> asValues(SCompany v) {
		PList<Expr<?>> r = PList.empty();
		r = r.plus(Sql.val(v.getId()));
		r = r.plusAll(_Address.asValues(v.getAdres()));
		return r;
	}
	
	public PList<Expr<?>> _expand() {
		PList<Expr<?>> res = PList.empty();
		res = res.plusAll(id._expand());
		res = res.plusAll(adres._expand());
		return res;
	}
	public SCompany read(RowReader _rowReader, ExprRowReaderCache _cache) {
		Integer id    = this.id.read(_rowReader,_cache);
		Address adres = this.adres.read(_rowReader, _cache);
		if(id == null || adres == null) { return null; }
		return _cache.updatedFromCache(SCompany.build(b-> b
			.setId(id)
			.setAdres(adres)
		));
	}
	public Optional<Expr<?>> _getAutoGenKey() {
		return Optional.of(id);
	}
	public SCompany _setAutoGenKey(SCompany object, Object value) {
		return object.withId((Integer) value);
	}
	public DbWork<SCompany> insert(SCompany newRow) {
		return Insert.into(this,val(newRow)).withGeneratedKeys(_getAutoGenKey().get())
			.map(key -> _setAutoGenKey(newRow,key));
	}
	public DbWork<SCompany> selectById(Integer id) {
		return Query.from(this).where(this.id.eq(id)).selection(this).flatMap(l -> Result.fromOpt(l.headOpt()));
	}
	public DbWork<Integer> deleteById(Integer id) {
		return new Delete(this).where(this.id.eq(id));
	}
	public DbWork<SCompany> update(SCompany _row) {
		return new Update(this)
			.set(adres, new ExprValueTable(adres, _row.getAdres()))
		.where(this.id.eq(_row.getId()))
		.flatMap(count -> count == 0
			? Result.empty()
			: count == 1 ? Result.success(_row) : Result.failure("More than one record update: " + count)
		);
	}
}
