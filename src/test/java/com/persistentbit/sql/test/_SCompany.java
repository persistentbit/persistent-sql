// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-15T11:05:17.112

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
 * This immutable value class contains the data for a record in the table 'COMPANY'.<br>
 * Generated from the database on 2017-01-15T11:04:40.707<br>
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
	public ETypeNumber<Integer> id = new ExprPropertyNumber<>(Integer.class,this,"id", "ID");
	public ETypeString adresStreet = new ExprPropertyString(this,"adresStreet", "ADRES_STREET");
	public ETypeNumber<Integer> adresHouseNumber = new ExprPropertyNumber<>(Integer.class,this,"adresHouseNumber", "ADRES_HOUSE_NUMBER");
	public ETypeString adresBusNumber = new ExprPropertyString(this,"adresBusNumber", "ADRES_BUS_NUMBER");
	public ETypeString adresPostalcode = new ExprPropertyString(this,"adresPostalcode", "ADRES_POSTALCODE");
	public ETypeString adresCity = new ExprPropertyString(this,"adresCity", "ADRES_CITY");
	public ETypeString adresCountry = new ExprPropertyString(this,"adresCountry", "ADRES_COUNTRY");
	
	@Override
	public PList<Tuple2<String,Expr<?>>> _all() {
		return PList.val(Tuple2.of("id",id), Tuple2.of("adresStreet",adresStreet), Tuple2.of("adresHouseNumber",adresHouseNumber), Tuple2.of("adresBusNumber",adresBusNumber), Tuple2.of("adresPostalcode",adresPostalcode), Tuple2.of("adresCity",adresCity), Tuple2.of("adresCountry",adresCountry));
	}
	
	public PList<Expr<?>> _asExprValues(SCompany v) {
		return _SCompany.asValues(v);
	}
	static public PList<Expr<?>> asValues(SCompany v) {
		PList<Expr<?>> r = PList.empty();
		r = r.plus(Sql.val(v.getId()));
		r = r.plus(Sql.val(v.getAdresStreet()));
		r = r.plus(Sql.val(v.getAdresHouseNumber()));
		r = r.plus(Sql.val(v.getAdresBusNumber().orElse(null)));
		r = r.plus(Sql.val(v.getAdresPostalcode()));
		r = r.plus(Sql.val(v.getAdresCity()));
		r = r.plus(Sql.val(v.getAdresCountry()));
		return r;
	}
	
	public PList<Expr<?>> _expand() {
		PList<Expr<?>> res = PList.empty();
		res = res.plusAll(id._expand());
		res = res.plusAll(adresStreet._expand());
		res = res.plusAll(adresHouseNumber._expand());
		res = res.plusAll(adresBusNumber._expand());
		res = res.plusAll(adresPostalcode._expand());
		res = res.plusAll(adresCity._expand());
		res = res.plusAll(adresCountry._expand());
		return res;
	}
	public SCompany read(RowReader _rowReader, ExprRowReaderCache _cache) {
		Integer id = this.id.read(_rowReader,_cache);
		String adresStreet = this.adresStreet.read(_rowReader,_cache);
		Integer adresHouseNumber = this.adresHouseNumber.read(_rowReader,_cache);
		String adresBusNumber = this.adresBusNumber.read(_rowReader,_cache);
		String adresPostalcode = this.adresPostalcode.read(_rowReader,_cache);
		String adresCity = this.adresCity.read(_rowReader,_cache);
		String adresCountry = this.adresCountry.read(_rowReader,_cache);
		if(id==null || adresStreet==null || adresHouseNumber==null || adresPostalcode==null || adresCity==null || adresCountry==null) { return null; }
		return _cache.updatedFromCache(SCompany.build(b-> b
			.setId(id)
			.setAdresStreet(adresStreet)
			.setAdresHouseNumber(adresHouseNumber)
			.setAdresBusNumber(adresBusNumber)
			.setAdresPostalcode(adresPostalcode)
			.setAdresCity(adresCity)
			.setAdresCountry(adresCountry)
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
		.set(adresStreet, Sql.val(_row.getAdresStreet()))
		.set(adresHouseNumber, Sql.val(_row.getAdresHouseNumber()))
		.set(adresBusNumber, Sql.val(_row.getAdresBusNumber().orElse(null)))
		.set(adresPostalcode, Sql.val(_row.getAdresPostalcode()))
		.set(adresCity, Sql.val(_row.getAdresCity()))
		.set(adresCountry, Sql.val(_row.getAdresCountry()))
		.where(this.id.eq(_row.getId()))
		.flatMap(count -> count == 0
			? Result.empty()
			: count == 1 ? Result.success(_row) : Result.failure("More than one record update: " + count)
		);
	}
}
