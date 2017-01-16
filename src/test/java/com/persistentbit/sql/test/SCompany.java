// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.017

package com.persistentbit.sql.test;

import com.persistentbit.core.utils.builders.NOT;
import com.persistentbit.core.utils.builders.SET;

import java.util.Objects;
import java.util.function.Function;

/**
 * 
 * This immutable value class contains the data for a record in the table 'COMPANY'.<br>
 * Generated from the database on 2017-01-15T11:05:16.988<br>
 * 
 */
public class SCompany {
	private final int     id;
	private final Address adres;

	public SCompany(int id, Address adres) {
		this.id = id;
		this.adres = Objects.requireNonNull(adres, "adres in SCompany can't be null");
	}
	public int getId() { return id; }

	public SCompany withId(int id) { return new SCompany(id, this.adres); }

	public Address getAdres() { return adres; }

	public SCompany withAdres(Address adres) { return new SCompany(this.id, adres); }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		SCompany that = (SCompany)o;
		
		if(id != that.id) return false;
		if(!adres.equals(that.adres)) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int result;
		result = Integer.hashCode(id);
		result = 31 * result + adres.hashCode();
		return result;
	}
	@Override
	public String toString() {
		return "SCompany<<" +
			"id=" + id +
			", adres=" + adres +
			">>";
	}

	static public class Builder<_T1, _T2>{
		private int     id;
		private Address adres;

		public Builder<SET, _T2> setId(int id) {
			this.id = id;
			return (Builder<SET, _T2>) this;
		}

		public Builder<_T1, SET> setAdres(Address adres) {
			this.adres = adres;
			return (Builder<_T1, SET>) this;
		}
	}

	static public SCompany build(Function<Builder<NOT, NOT>, Builder<SET, SET>> supplier) {
		Builder b = supplier.apply(new Builder<>());
		return new SCompany(b.id, b.adres);
	}
}
