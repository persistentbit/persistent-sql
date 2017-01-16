// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.120

package com.persistentbit.sql.test;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.result.Result;
import com.persistentbit.core.tuples.Tuple2;
import com.persistentbit.sql.staticsql.*;
import com.persistentbit.sql.staticsql.expr.*;

import java.util.Optional;

/**
 * 
 * This immutable value class contains the data for a record in the table 'INVOICE_LINE'.<br>
 * Generated from the database on 2017-01-15T11:05:17.015<br>
 * 
 */
public class _SInvoiceLine implements ETypeObject<SInvoiceLine> {
	private final ETypePropertyParent __parent;
	public _SInvoiceLine(ETypePropertyParent parent) {
		this.__parent = parent;
	}
	
	public _SInvoiceLine() {
		this(null);
	}
	
	@Override
	public Optional<ETypePropertyParent> getParent() { return Optional.ofNullable(this.__parent); }
	
	@Override
	public String _getTableName() {
		return "INVOICE_LINE";
	}
	
	@Override
	public String toString() { return _getTableName(); }
	
	
	@Override
	public String _fullColumnName(ExprToSqlContext context) { return __parent == null ? "" : __parent._fullColumnName(context); }
	public ETypeNumber<Integer> id = new ExprPropertyNumber<>(Integer.class,this,"id", "ID");
	public ETypeNumber<Integer> invoiceId = new ExprPropertyNumber<>(Integer.class,this,"invoiceId", "INVOICE_ID");
	public ETypeString product = new ExprPropertyString(this,"product", "PRODUCT");
	
	@Override
	public PList<Tuple2<String,Expr<?>>> _all() {
		return PList.val(Tuple2.of("id",id), Tuple2.of("invoiceId",invoiceId), Tuple2.of("product",product));
	}
	
	public PList<Expr<?>> _asExprValues(SInvoiceLine v) {
		return _SInvoiceLine.asValues(v);
	}
	static public PList<Expr<?>> asValues(SInvoiceLine v) {
		PList<Expr<?>> r = PList.empty();
		r = r.plus(Sql.val(v.getId()));
		r = r.plus(Sql.val(v.getInvoiceId()));
		r = r.plus(Sql.val(v.getProduct().orElse(null)));
		return r;
	}
	
	public PList<Expr<?>> _expand() {
		PList<Expr<?>> res = PList.empty();
		res = res.plusAll(id._expand());
		res = res.plusAll(invoiceId._expand());
		res = res.plusAll(product._expand());
		return res;
	}
	public SInvoiceLine read(RowReader _rowReader, ExprRowReaderCache _cache) {
		Integer id = this.id.read(_rowReader,_cache);
		Integer invoiceId = this.invoiceId.read(_rowReader,_cache);
		String product = this.product.read(_rowReader,_cache);
		if(id==null || invoiceId==null) { return null; }
		return _cache.updatedFromCache(SInvoiceLine.build(b-> b
			.setId(id)
			.setInvoiceId(invoiceId)
			.setProduct(product)
		));
	}
	public Optional<Expr<?>> _getAutoGenKey() {
		return Optional.of(id);
	}
	public SInvoiceLine _setAutoGenKey(SInvoiceLine object, Object value) {
		return object.withId((Integer) value);
	}
	public DbWork<SInvoiceLine> insert(SInvoiceLine newRow) {
		return Insert.into(this,val(newRow)).withGeneratedKeys(_getAutoGenKey().get())
			.map(key -> _setAutoGenKey(newRow,key));
	}
	public DbWork<SInvoiceLine> selectById(Integer id) {
		return Query.from(this).where(this.id.eq(id)).selection(this).flatMap(l -> Result.fromOpt(l.headOpt()));
	}
	public DbWork<Integer> deleteById(Integer id) {
		return new Delete(this).where(this.id.eq(id));
	}
	public DbWork<SInvoiceLine> update(SInvoiceLine _row) {
		return new Update(this)
		.set(invoiceId, Sql.val(_row.getInvoiceId()))
		.set(product, Sql.val(_row.getProduct().orElse(null)))
		.where(this.id.eq(_row.getId()))
		.flatMap(count -> count == 0
			? Result.empty()
			: count == 1 ? Result.success(_row) : Result.failure("More than one record update: " + count)
		);
	}
}
