// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.115

package com.persistentbit.sql.test;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.*;
import com.persistentbit.sql.staticsql.expr.*;

import java.util.Optional;

/**
 * 
 * This immutable value class contains the data for a record in the table 'INVOICE'.<br>
 * Generated from the database on 2017-01-15T11:05:17.011<br>
 * 
 */
public class _SInvoice implements ETypeObject<SInvoice> {
	private final ETypePropertyParent __parent;
	public _SInvoice(ETypePropertyParent parent) {
		this.__parent = parent;
	}
	
	public _SInvoice() {
		this(null);
	}
	
	@Override
	public Optional<ETypePropertyParent> getParent() { return Optional.ofNullable(this.__parent); }
	
	@Override
	public String _getTableName() {
		return "INVOICE";
	}
	
	@Override
	public String toString() { return _getTableName(); }
	
	
	@Override
	public String _fullColumnName(ExprToSqlContext context) { return __parent == null ? "" : __parent._fullColumnName(context); }
	public ETypeNumber<Integer> id = new ExprPropertyNumber<>(Integer.class,this,"id", "ID");
	public ETypeString invoiceNummer = new ExprPropertyString(this,"invoiceNummer", "INVOICE_NUMMER");
	public ETypeNumber<Integer> fromCompanyId = new ExprPropertyNumber<>(Integer.class,this,"fromCompanyId", "FROM_COMPANY_ID");
	public ETypeNumber<Integer> toCompanyId = new ExprPropertyNumber<>(Integer.class,this,"toCompanyId", "TO_COMPANY_ID");
	
	@Override
	public PList<Tuple2<String,Expr<?>>> _all() {
		return PList.val(Tuple2.of("id",id), Tuple2.of("invoiceNummer",invoiceNummer), Tuple2.of("fromCompanyId",fromCompanyId), Tuple2.of("toCompanyId",toCompanyId));
	}
	
	public PList<Expr<?>> _asExprValues(SInvoice v) {
		return _SInvoice.asValues(v);
	}
	static public PList<Expr<?>> asValues(SInvoice v) {
		PList<Expr<?>> r = PList.empty();
		r = r.plus(Sql.val(v.getId()));
		r = r.plus(Sql.val(v.getInvoiceNummer()));
		r = r.plus(Sql.val(v.getFromCompanyId()));
		r = r.plus(Sql.val(v.getToCompanyId()));
		return r;
	}
	
	public PList<Expr<?>> _expand() {
		PList<Expr<?>> res = PList.empty();
		res = res.plusAll(id._expand());
		res = res.plusAll(invoiceNummer._expand());
		res = res.plusAll(fromCompanyId._expand());
		res = res.plusAll(toCompanyId._expand());
		return res;
	}
	public SInvoice read(RowReader _rowReader, ExprRowReaderCache _cache) {
		Integer id = this.id.read(_rowReader,_cache);
		String invoiceNummer = this.invoiceNummer.read(_rowReader,_cache);
		Integer fromCompanyId = this.fromCompanyId.read(_rowReader,_cache);
		Integer toCompanyId = this.toCompanyId.read(_rowReader,_cache);
		if(id==null || invoiceNummer==null || fromCompanyId==null || toCompanyId==null) { return null; }
		return _cache.updatedFromCache(SInvoice.build(b-> b
			.setId(id)
			.setInvoiceNummer(invoiceNummer)
			.setFromCompanyId(fromCompanyId)
			.setToCompanyId(toCompanyId)
		));
	}
	public Optional<Expr<?>> _getAutoGenKey() {
		return Optional.of(id);
	}
	public SInvoice _setAutoGenKey(SInvoice object, Object value) {
		return object.withId((Integer) value);
	}
	public DbWork<SInvoice> insert(SInvoice newRow) {
		return Insert.into(this,val(newRow)).withGeneratedKeys(_getAutoGenKey().get())
			.map(key -> _setAutoGenKey(newRow,key));
	}
	public DbWork<SInvoice> selectById(Integer id) {
		return Query.from(this).where(this.id.eq(id)).selection(this).flatMap(l -> Result.fromOpt(l.headOpt()));
	}
	public DbWork<Integer> deleteById(Integer id) {
		return new Delete(this).where(this.id.eq(id));
	}
	public DbWork<SInvoice> update(SInvoice _row) {
		return new Update(this)
		.set(invoiceNummer, Sql.val(_row.getInvoiceNummer()))
		.set(fromCompanyId, Sql.val(_row.getFromCompanyId()))
		.set(toCompanyId, Sql.val(_row.getToCompanyId()))
		.where(this.id.eq(_row.getId()))
		.flatMap(count -> count == 0
			? Result.empty()
			: count == 1 ? Result.success(_row) : Result.failure("More than one record update: " + count)
		);
	}
}
