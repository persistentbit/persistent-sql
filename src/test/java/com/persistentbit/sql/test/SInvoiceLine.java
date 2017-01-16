// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.029

package com.persistentbit.sql.test;

import com.persistentbit.core.Nullable;
import com.persistentbit.core.utils.builders.NOT;
import com.persistentbit.core.utils.builders.SET;

import java.util.Optional;
import java.util.function.Function;

/**
 * 
 * This immutable value class contains the data for a record in the table 'INVOICE_LINE'.<br>
 * Generated from the database on 2017-01-15T11:05:17.015<br>
 * 
 */
public class SInvoiceLine {
	private final int id;
	private final int invoiceId;
	@Nullable private final String product;
	
	public SInvoiceLine(int id, int invoiceId, String product) {
		this.id = id;
		this.invoiceId = invoiceId;
		this.product = product;
	}
	public SInvoiceLine(int id, int invoiceId) {
		this(id,invoiceId,null);
	}
	public int getId() { return id; }
	public SInvoiceLine withId(int id) { return new SInvoiceLine(id, this.invoiceId, this.product); }
	
	public int getInvoiceId() { return invoiceId; }
	public SInvoiceLine withInvoiceId(int invoiceId) { return new SInvoiceLine(this.id, invoiceId, this.product); }
	
	public Optional<String> getProduct() { return Optional.ofNullable(product); }
	public SInvoiceLine withProduct(String product) { return new SInvoiceLine(this.id, this.invoiceId, product); }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		SInvoiceLine that = (SInvoiceLine)o;
		
		if(id != that.id) return false;
		if(invoiceId != that.invoiceId) return false;
		if(product!= null ? !product.equals(that.product) : that.product != null) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int result;
		result = Integer.hashCode(id);
		result = 31 * result + Integer.hashCode(invoiceId);
		result = 31 * result + (product != null ? product.hashCode(): 0);
		return result;
	}
	@Override
	public String toString() {
		return "SInvoiceLine<<" +
			"id=" + id +
			", invoiceId=" + invoiceId +
			(product == null ? "" : ", product=" + product) +
			">>";
	}
	
	static public class Builder<_T1,_T2> {
		private int id;
		private int invoiceId;
		@Nullable private String product;
		
		public Builder<SET,_T2> setId(int id) {
			this.id = id;
			return (Builder<SET,_T2>) this;
		}
		public Builder<_T1,SET> setInvoiceId(int invoiceId) {
			this.invoiceId = invoiceId;
			return (Builder<_T1,SET>) this;
		}
		public Builder<_T1,_T2> setProduct(String product) {
			this.product = product;
			return (Builder<_T1,_T2>) this;
		}
	}
	static public  SInvoiceLine build(Function<Builder<NOT,NOT>,Builder<SET,SET>> supplier) {
		Builder b = supplier.apply(new Builder<>());
		return new SInvoiceLine(b.id, b.invoiceId, b.product);
	}
}
