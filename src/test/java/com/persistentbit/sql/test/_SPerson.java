// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.123

package com.persistentbit.sql.test;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.*;
import com.persistentbit.sql.staticsql.expr.*;

import java.util.Optional;

/**
 * 
 * This immutable value class contains the data for a record in the table 'PERSON'.<br>
 * Generated from the database on 2017-01-15T11:05:17.019<br>
 * 
 */
public class _SPerson implements ETypeObject<SPerson> {
	private final ETypePropertyParent __parent;
	public _SPerson(ETypePropertyParent parent) {
		this.__parent = parent;
	}
	
	public _SPerson() {
		this(null);
	}
	
	@Override
	public Optional<ETypePropertyParent> getParent() { return Optional.ofNullable(this.__parent); }
	
	@Override
	public String _getTableName() {
		return "PERSON";
	}
	
	@Override
	public String toString() { return _getTableName(); }
	
	
	@Override
	public String _fullColumnName(ExprToSqlContext context) { return __parent == null ? "" : __parent._fullColumnName(context); }
	public ETypeNumber<Integer> id       = new ExprPropertyNumber<>(Integer.class,this,"id", "ID");
	public ETypeString          userName = new ExprPropertyString(this,"userName", "USER_NAME");
	public ETypeString          password = new ExprPropertyString(this,"password", "PASSWORD");
	public _Address             address  = new _Address(new ExprProperty(Address.class, this, "address", ""));
	
	@Override
	public PList<Tuple2<String,Expr<?>>> _all() {
		return PList.val(Tuple2.of("id", id), Tuple2.of("userName", userName), Tuple2.of("password", password), Tuple2
			.of("address", address));
	}
	
	public PList<Expr<?>> _asExprValues(SPerson v) {
		return _SPerson.asValues(v);
	}
	static public PList<Expr<?>> asValues(SPerson v) {
		PList<Expr<?>> r = PList.empty();
		r = r.plus(Sql.val(v.getId()));
		r = r.plus(Sql.val(v.getUserName()));
		r = r.plus(Sql.val(v.getPassword()));
		r = r.plusAll(_Address.asValues(v.getAddress()));
		return r;
	}
	
	public PList<Expr<?>> _expand() {
		PList<Expr<?>> res = PList.empty();
		res = res.plusAll(id._expand());
		res = res.plusAll(userName._expand());
		res = res.plusAll(password._expand());
		res = res.plusAll(address._expand());
		return res;
	}
	public SPerson read(RowReader _rowReader, ExprRowReaderCache _cache) {
		Integer id       = this.id.read(_rowReader,_cache);
		String  userName = this.userName.read(_rowReader,_cache);
		String  password = this.password.read(_rowReader,_cache);
		Address address  = this.address.read(_rowReader, _cache);
		if(id == null || userName == null || password == null || address == null) { return null; }
		return _cache.updatedFromCache(SPerson.build(b-> b
			.setId(id)
			.setUserName(userName)
			.setPassword(password)
			.setAddress(address)
		));
	}
	public Optional<Expr<?>> _getAutoGenKey() {
		return Optional.of(id);
	}
	public SPerson _setAutoGenKey(SPerson object, Object value) {
		return object.withId((Integer) value);
	}
	public DbWork<SPerson> insert(SPerson newRow) {
		return Insert.into(this,val(newRow)).withGeneratedKeys(_getAutoGenKey().get())
			.map(key -> _setAutoGenKey(newRow,key));
	}
	public DbWork<SPerson> selectById(Integer id) {
		return Query.from(this).where(this.id.eq(id)).selection(this).flatMap(l -> Result.fromOpt(l.headOpt()));
	}
	public DbWork<Integer> deleteById(Integer id) {
		return new Delete(this).where(this.id.eq(id));
	}
	public DbWork<SPerson> update(SPerson _row) {
		return new Update(this)
		.set(userName, Sql.val(_row.getUserName()))
		.set(password, Sql.val(_row.getPassword()))
			.set(address, new ExprValueTable(address, _row.getAddress()))
		.where(this.id.eq(_row.getId()))
		.flatMap(count -> count == 0
			? Result.empty()
			: count == 1 ? Result.success(_row) : Result.failure("More than one record update: " + count)
		);
	}

}
