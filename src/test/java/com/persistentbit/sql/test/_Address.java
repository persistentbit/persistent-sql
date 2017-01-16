// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.055

package com.persistentbit.sql.test;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.staticsql.ExprRowReaderCache;
import com.persistentbit.sql.staticsql.RowReader;
import com.persistentbit.sql.staticsql.expr.*;

import java.util.Optional;

public class _Address implements ETypeObject<Address> {
	private final ETypePropertyParent __parent;
	public _Address(ETypePropertyParent parent) {
		this.__parent = parent;
	}
	
	public _Address() {
		this(null);
	}
	
	@Override
	public Optional<ETypePropertyParent> getParent() { return Optional.ofNullable(this.__parent); }
	
	@Override
	public String _getTableName() {
		return "ADDRESS";
	}
	
	@Override
	public String toString() { return _getTableName(); }
	
	
	@Override
	public String _fullColumnName(ExprToSqlContext context) { return __parent == null ? "" : __parent._fullColumnName(context); }
	public ETypeString street = new ExprPropertyString(this,"street", "STREET");
	public ETypeNumber<Integer> houseNumber = new ExprPropertyNumber<>(Integer.class,this,"houseNumber", "HOUSE_NUMBER");
	public ETypeString busNumber = new ExprPropertyString(this,"busNumber", "BUS_NUMBER");
	public ETypeString postalcode = new ExprPropertyString(this,"postalcode", "POSTALCODE");
	public ETypeString city = new ExprPropertyString(this,"city", "CITY");
	public ETypeString country = new ExprPropertyString(this,"country", "COUNTRY");
	
	@Override
	public PList<Tuple2<String,Expr<?>>> _all() {
		return PList.val(Tuple2.of("street",street), Tuple2.of("houseNumber",houseNumber), Tuple2.of("busNumber",busNumber), Tuple2.of("postalcode",postalcode), Tuple2.of("city",city), Tuple2.of("country",country));
	}
	
	public PList<Expr<?>> _asExprValues(Address v) {
		return _Address.asValues(v);
	}
	static public PList<Expr<?>> asValues(Address v) {
		PList<Expr<?>> r = PList.empty();
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
		res = res.plusAll(street._expand());
		res = res.plusAll(houseNumber._expand());
		res = res.plusAll(busNumber._expand());
		res = res.plusAll(postalcode._expand());
		res = res.plusAll(city._expand());
		res = res.plusAll(country._expand());
		return res;
	}
	public Address read(RowReader _rowReader, ExprRowReaderCache _cache) {
		String street = this.street.read(_rowReader,_cache);
		Integer houseNumber = this.houseNumber.read(_rowReader,_cache);
		String busNumber = this.busNumber.read(_rowReader,_cache);
		String postalcode = this.postalcode.read(_rowReader,_cache);
		String city = this.city.read(_rowReader,_cache);
		String country = this.country.read(_rowReader,_cache);
		if(street==null || houseNumber==null || postalcode==null || city==null || country==null) { return null; }
		return _cache.updatedFromCache(Address.build(b-> b
			.setStreet(street)
			.setHouseNumber(houseNumber)
			.setBusNumber(busNumber)
			.setPostalcode(postalcode)
			.setCity(city)
			.setCountry(country)
		));
	}
	public Optional<Expr<?>> _getAutoGenKey() {
		return Optional.empty();
	}
	public Address _setAutoGenKey(Address object, Object value) {
		throw new PersistSqlException(" There is no auto generated key for Address");
	}
}
