// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-15T11:05:17.075

package com.persistentbit.sql.test;

import com.persistentbit.core.utils.builders.SET;
import com.persistentbit.core.utils.builders.NOT;
import java.util.function.Function;
import java.util.Optional;
import com.persistentbit.core.Nullable;
import java.util.Objects;

/**
 * 
 * This immutable value class contains the data for a record in the table 'COMPANY'.<br>
 * Generated from the database on 2017-01-15T11:04:40.707<br>
 * 
 */
public class SCompany {
	private final int id;
	private final String adresStreet;
	private final int adresHouseNumber;
	@Nullable private final String adresBusNumber;
	private final String adresPostalcode;
	private final String adresCity;
	private final String adresCountry;
	
	public SCompany(int id, String adresStreet, int adresHouseNumber, String adresBusNumber, String adresPostalcode, String adresCity, String adresCountry) {
		this.id = id;
		this.adresStreet = Objects.requireNonNull(adresStreet,"adresStreet in SCompany can't be null");
		this.adresHouseNumber = adresHouseNumber;
		this.adresBusNumber = adresBusNumber;
		this.adresPostalcode = Objects.requireNonNull(adresPostalcode,"adresPostalcode in SCompany can't be null");
		this.adresCity = Objects.requireNonNull(adresCity,"adresCity in SCompany can't be null");
		this.adresCountry = Objects.requireNonNull(adresCountry,"adresCountry in SCompany can't be null");
	}
	public SCompany(int id, String adresStreet, int adresHouseNumber, String adresPostalcode, String adresCity, String adresCountry) {
		this(id,adresStreet,adresHouseNumber,null,adresPostalcode,adresCity,adresCountry);
	}
	public int getId() { return id; }
	public SCompany withId(int id) { return new SCompany(id, this.adresStreet, this.adresHouseNumber, this.adresBusNumber, this.adresPostalcode, this.adresCity, this.adresCountry); }
	
	public String getAdresStreet() { return adresStreet; }
	public SCompany withAdresStreet(String adresStreet) { return new SCompany(this.id, adresStreet, this.adresHouseNumber, this.adresBusNumber, this.adresPostalcode, this.adresCity, this.adresCountry); }
	
	public int getAdresHouseNumber() { return adresHouseNumber; }
	public SCompany withAdresHouseNumber(int adresHouseNumber) { return new SCompany(this.id, this.adresStreet, adresHouseNumber, this.adresBusNumber, this.adresPostalcode, this.adresCity, this.adresCountry); }
	
	public Optional<String> getAdresBusNumber() { return Optional.ofNullable(adresBusNumber); }
	public SCompany withAdresBusNumber(String adresBusNumber) { return new SCompany(this.id, this.adresStreet, this.adresHouseNumber, adresBusNumber, this.adresPostalcode, this.adresCity, this.adresCountry); }
	
	public String getAdresPostalcode() { return adresPostalcode; }
	public SCompany withAdresPostalcode(String adresPostalcode) { return new SCompany(this.id, this.adresStreet, this.adresHouseNumber, this.adresBusNumber, adresPostalcode, this.adresCity, this.adresCountry); }
	
	public String getAdresCity() { return adresCity; }
	public SCompany withAdresCity(String adresCity) { return new SCompany(this.id, this.adresStreet, this.adresHouseNumber, this.adresBusNumber, this.adresPostalcode, adresCity, this.adresCountry); }
	
	public String getAdresCountry() { return adresCountry; }
	public SCompany withAdresCountry(String adresCountry) { return new SCompany(this.id, this.adresStreet, this.adresHouseNumber, this.adresBusNumber, this.adresPostalcode, this.adresCity, adresCountry); }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		SCompany that = (SCompany)o;
		
		if(id != that.id) return false;
		if(!adresStreet.equals(that.adresStreet)) return false;
		if(adresHouseNumber != that.adresHouseNumber) return false;
		if(adresBusNumber!= null ? !adresBusNumber.equals(that.adresBusNumber) : that.adresBusNumber != null) return false;
		if(!adresPostalcode.equals(that.adresPostalcode)) return false;
		if(!adresCity.equals(that.adresCity)) return false;
		if(!adresCountry.equals(that.adresCountry)) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int result;
		result = Integer.hashCode(id);
		result = 31 * result + adresStreet.hashCode();
		result = 31 * result + Integer.hashCode(adresHouseNumber);
		result = 31 * result + (adresBusNumber != null ? adresBusNumber.hashCode(): 0);
		result = 31 * result + adresPostalcode.hashCode();
		result = 31 * result + adresCity.hashCode();
		result = 31 * result + adresCountry.hashCode();
		return result;
	}
	@Override
	public String toString() {
		return "SCompany<<" +
			"id=" + id +
			", adresStreet=" + adresStreet +
			", adresHouseNumber=" + adresHouseNumber +
			(adresBusNumber == null ? "" : ", adresBusNumber=" + adresBusNumber) +
			", adresPostalcode=" + adresPostalcode +
			", adresCity=" + adresCity +
			", adresCountry=" + adresCountry +
			">>";
	}
	
	static public class Builder<_T1,_T2,_T3,_T4,_T5,_T6> {
		private int id;
		private String adresStreet;
		private int adresHouseNumber;
		@Nullable private String adresBusNumber;
		private String adresPostalcode;
		private String adresCity;
		private String adresCountry;
		
		public Builder<SET,_T2,_T3,_T4,_T5,_T6> setId(int id) {
			this.id = id;
			return (Builder<SET,_T2,_T3,_T4,_T5,_T6>) this;
		}
		public Builder<_T1,SET,_T3,_T4,_T5,_T6> setAdresStreet(String adresStreet) {
			this.adresStreet = adresStreet;
			return (Builder<_T1,SET,_T3,_T4,_T5,_T6>) this;
		}
		public Builder<_T1,_T2,SET,_T4,_T5,_T6> setAdresHouseNumber(int adresHouseNumber) {
			this.adresHouseNumber = adresHouseNumber;
			return (Builder<_T1,_T2,SET,_T4,_T5,_T6>) this;
		}
		public Builder<_T1,_T2,_T3,_T4,_T5,_T6> setAdresBusNumber(String adresBusNumber) {
			this.adresBusNumber = adresBusNumber;
			return (Builder<_T1,_T2,_T3,_T4,_T5,_T6>) this;
		}
		public Builder<_T1,_T2,_T3,SET,_T5,_T6> setAdresPostalcode(String adresPostalcode) {
			this.adresPostalcode = adresPostalcode;
			return (Builder<_T1,_T2,_T3,SET,_T5,_T6>) this;
		}
		public Builder<_T1,_T2,_T3,_T4,SET,_T6> setAdresCity(String adresCity) {
			this.adresCity = adresCity;
			return (Builder<_T1,_T2,_T3,_T4,SET,_T6>) this;
		}
		public Builder<_T1,_T2,_T3,_T4,_T5,SET> setAdresCountry(String adresCountry) {
			this.adresCountry = adresCountry;
			return (Builder<_T1,_T2,_T3,_T4,_T5,SET>) this;
		}
	}
	static public  SCompany build(Function<Builder<NOT,NOT,NOT,NOT,NOT,NOT>,Builder<SET,SET,SET,SET,SET,SET>> supplier) {
		Builder b = supplier.apply(new Builder<>());
		return new SCompany(b.id, b.adresStreet, b.adresHouseNumber, b.adresBusNumber, b.adresPostalcode, b.adresCity, b.adresCountry);
	}
}
