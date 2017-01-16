// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.025

package com.persistentbit.sql.test;

import com.persistentbit.core.utils.builders.NOT;
import com.persistentbit.core.utils.builders.SET;

import java.util.Objects;
import java.util.function.Function;

/**
 * 
 * This immutable value class contains the data for a record in the table 'INVOICE'.<br>
 * Generated from the database on 2017-01-15T11:05:17.011<br>
 * 
 */
public class SInvoice {
	private final int id;
	private final String invoiceNummer;
	private final int fromCompanyId;
	private final int toCompanyId;
	
	public SInvoice(int id, String invoiceNummer, int fromCompanyId, int toCompanyId) {
		this.id = id;
		this.invoiceNummer = Objects.requireNonNull(invoiceNummer,"invoiceNummer in SInvoice can't be null");
		this.fromCompanyId = fromCompanyId;
		this.toCompanyId = toCompanyId;
	}
	public int getId() { return id; }
	public SInvoice withId(int id) { return new SInvoice(id, this.invoiceNummer, this.fromCompanyId, this.toCompanyId); }
	
	public String getInvoiceNummer() { return invoiceNummer; }
	public SInvoice withInvoiceNummer(String invoiceNummer) { return new SInvoice(this.id, invoiceNummer, this.fromCompanyId, this.toCompanyId); }
	
	public int getFromCompanyId() { return fromCompanyId; }
	public SInvoice withFromCompanyId(int fromCompanyId) { return new SInvoice(this.id, this.invoiceNummer, fromCompanyId, this.toCompanyId); }
	
	public int getToCompanyId() { return toCompanyId; }
	public SInvoice withToCompanyId(int toCompanyId) { return new SInvoice(this.id, this.invoiceNummer, this.fromCompanyId, toCompanyId); }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		SInvoice that = (SInvoice)o;
		
		if(id != that.id) return false;
		if(!invoiceNummer.equals(that.invoiceNummer)) return false;
		if(fromCompanyId != that.fromCompanyId) return false;
		if(toCompanyId != that.toCompanyId) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int result;
		result = Integer.hashCode(id);
		result = 31 * result + invoiceNummer.hashCode();
		result = 31 * result + Integer.hashCode(fromCompanyId);
		result = 31 * result + Integer.hashCode(toCompanyId);
		return result;
	}
	@Override
	public String toString() {
		return "SInvoice<<" +
			"id=" + id +
			", invoiceNummer=" + invoiceNummer +
			", fromCompanyId=" + fromCompanyId +
			", toCompanyId=" + toCompanyId +
			">>";
	}
	
	static public class Builder<_T1,_T2,_T3,_T4> {
		private int id;
		private String invoiceNummer;
		private int fromCompanyId;
		private int toCompanyId;
		
		public Builder<SET,_T2,_T3,_T4> setId(int id) {
			this.id = id;
			return (Builder<SET,_T2,_T3,_T4>) this;
		}
		public Builder<_T1,SET,_T3,_T4> setInvoiceNummer(String invoiceNummer) {
			this.invoiceNummer = invoiceNummer;
			return (Builder<_T1,SET,_T3,_T4>) this;
		}
		public Builder<_T1,_T2,SET,_T4> setFromCompanyId(int fromCompanyId) {
			this.fromCompanyId = fromCompanyId;
			return (Builder<_T1,_T2,SET,_T4>) this;
		}
		public Builder<_T1,_T2,_T3,SET> setToCompanyId(int toCompanyId) {
			this.toCompanyId = toCompanyId;
			return (Builder<_T1,_T2,_T3,SET>) this;
		}
	}
	static public  SInvoice build(Function<Builder<NOT,NOT,NOT,NOT>,Builder<SET,SET,SET,SET>> supplier) {
		Builder b = supplier.apply(new Builder<>());
		return new SInvoice(b.id, b.invoiceNummer, b.fromCompanyId, b.toCompanyId);
	}
}
