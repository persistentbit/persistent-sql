// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-15T11:05:17.125

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
 * This immutable value class contains the data for a record in the table 'PERSON'.<br>
 * Generated from the database on 2017-01-15T11:04:40.734<br>
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
	public ETypeNumber<Integer> id = new ExprPropertyNumber<>(Integer.class,this,"id", "ID");
	public ETypeString userName = new ExprPropertyString(this,"userName", "USER_NAME");
	public ETypeString password = new ExprPropertyString(this,"password", "PASSWORD");
	public ETypeString street = new ExprPropertyString(this,"street", "STREET");
	public ETypeNumber<Integer> houseNumber = new ExprPropertyNumber<>(Integer.class,this,"houseNumber", "HOUSE_NUMBER");
	public ETypeString busNumber = new ExprPropertyString(this,"busNumber", "BUS_NUMBER");
	public ETypeString postalcode = new ExprPropertyString(this,"postalcode", "POSTALCODE");
	public ETypeString city = new ExprPropertyString(this,"city", "CITY");
	public ETypeString country = new ExprPropertyString(this,"country", "COUNTRY");
	
	@Override
	public PList<Tuple2<String,Expr<?>>> _all() {
		return PList.val(Tuple2.of("id",id), Tuple2.of("userName",userName), Tuple2.of("password",password), Tuple2.of("street",street), Tuple2.of("houseNumber",houseNumber), Tuple2.of("busNumber",busNumber), Tuple2.of("postalcode",postalcode), Tuple2.of("city",city), Tuple2.of("country",country));
	}
	
	public PList<Expr<?>> _asExprValues(SPerson v) {
		return _SPerson.asValues(v);
	}
	static public PList<Expr<?>> asValues(SPerson v) {
		PList<Expr<?>> r = PList.empty();
		r = r.plus(Sql.val(v.getId()));
		r = r.plus(Sql.val(v.getUserName()));
		r = r.plus(Sql.val(v.getPassword()));
		r = r.plus(Sql.val(v.getStreet()));
		r = r.plus(Sql.val(v.getHouseNumber()));
		r = r.plus(Sql.val(v.getBusNumber().orElse(null)));
		r = r.plus(Sql.val(v.getPostalcode()));
		r = r.plus(Sql.val(v.getCity()));
		r = r.plus(Sql.val(v.getCountry()));
		return r;
	}
	
	public PList<Expr<?>> _expand() {
		PList<Expr<?>> res = PList.empty();
		res = res.plusAll(id._expand());
		res = res.plusAll(userName._expand());
		res = res.plusAll(password._expand());
		res = res.plusAll(street._expand());
		res = res.plusAll(houseNumber._expand());
		res = res.plusAll(busNumber._expand());
		res = res.plusAll(postalcode._expand());
		res = res.plusAll(city._expand());
		res = res.plusAll(country._expand());
		return res;
	}
	public SPerson read(RowReader _rowReader, ExprRowReaderCache _cache) {
		Integer id = this.id.read(_rowReader,_cache);
		String userName = this.userName.read(_rowReader,_cache);
		String password = this.password.read(_rowReader,_cache);
		String street = this.street.read(_rowReader,_cache);
		Integer houseNumber = this.houseNumber.read(_rowReader,_cache);
		String busNumber = this.busNumber.read(_rowReader,_cache);
		String postalcode = this.postalcode.read(_rowReader,_cache);
		String city = this.city.read(_rowReader,_cache);
		String country = this.country.read(_rowReader,_cache);
		if(id==null || userName==null || password==null || street==null || houseNumber==null || postalcode==null || city==null || country==null) { return null; }
		return _cache.updatedFromCache(SPerson.build(b-> b
			.setId(id)
			.setUserName(userName)
			.setPassword(password)
			.setStreet(street)
			.setHouseNumber(houseNumber)
			.setBusNumber(busNumber)
			.setPostalcode(postalcode)
			.setCity(city)
			.setCountry(country)
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
		.set(street, Sql.val(_row.getStreet()))
		.set(houseNumber, Sql.val(_row.getHouseNumber()))
		.set(busNumber, Sql.val(_row.getBusNumber().orElse(null)))
		.set(postalcode, Sql.val(_row.getPostalcode()))
		.set(city, Sql.val(_row.getCity()))
		.set(country, Sql.val(_row.getCountry()))
		.where(this.id.eq(_row.getId()))
		.flatMap(count -> count == 0
			? Result.empty()
			: count == 1 ? Result.success(_row) : Result.failure("More than one record update: " + count)
		);
	}
}
