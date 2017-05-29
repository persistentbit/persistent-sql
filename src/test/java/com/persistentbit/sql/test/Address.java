// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:02.973

package com.persistentbit.sql.test;

import com.persistentbit.core.Nullable;
import com.persistentbit.core.utils.builders.NOT;
import com.persistentbit.core.utils.builders.SET;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class Address {
	private final String street;
	private final int houseNumber;
	@Nullable private final String busNumber;
	private final String postalcode;
	private final String city;
	private final String country;
	
	public Address(String street, int houseNumber, String busNumber, String postalcode, String city, String country) {
		this.street = Objects.requireNonNull(street,"street in Address can't be null");
		this.houseNumber = houseNumber;
		this.busNumber = busNumber;
		this.postalcode = Objects.requireNonNull(postalcode,"postalcode in Address can't be null");
		this.city = Objects.requireNonNull(city,"city in Address can't be null");
		this.country = Objects.requireNonNull(country,"country in Address can't be null");
	}
	public Address(String street, int houseNumber, String postalcode, String city, String country) {
		this(street,houseNumber,null,postalcode,city,country);
	}
	public String getStreet() { return street; }
	public Address withStreet(String street) { return new Address(street, this.houseNumber, this.busNumber, this.postalcode, this.city, this.country); }
	
	public int getHouseNumber() { return houseNumber; }
	public Address withHouseNumber(int houseNumber) { return new Address(this.street, houseNumber, this.busNumber, this.postalcode, this.city, this.country); }
	
	public Optional<String> getBusNumber() { return Optional.ofNullable(busNumber); }
	public Address withBusNumber(String busNumber) { return new Address(this.street, this.houseNumber, busNumber, this.postalcode, this.city, this.country); }
	
	public String getPostalcode() { return postalcode; }
	public Address withPostalcode(String postalcode) { return new Address(this.street, this.houseNumber, this.busNumber, postalcode, this.city, this.country); }
	
	public String getCity() { return city; }
	public Address withCity(String city) { return new Address(this.street, this.houseNumber, this.busNumber, this.postalcode, city, this.country); }
	
	public String getCountry() { return country; }
	public Address withCountry(String country) { return new Address(this.street, this.houseNumber, this.busNumber, this.postalcode, this.city, country); }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Address that = (Address)o;
		
		if(!street.equals(that.street)) return false;
		if(houseNumber != that.houseNumber) return false;
		if(busNumber!= null ? !busNumber.equals(that.busNumber) : that.busNumber != null) return false;
		if(!postalcode.equals(that.postalcode)) return false;
		if(!city.equals(that.city)) return false;
		if(!country.equals(that.country)) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int result;
		result = street.hashCode();
		result = 31 * result + Integer.hashCode(houseNumber);
		result = 31 * result + (busNumber != null ? busNumber.hashCode(): 0);
		result = 31 * result + postalcode.hashCode();
		result = 31 * result + city.hashCode();
		result = 31 * result + country.hashCode();
		return result;
	}
	@Override
	public String toString() {
		return "Address<<" +
			"street=" + street +
			", houseNumber=" + houseNumber +
			(busNumber == null ? "" : ", busNumber=" + busNumber) +
			", postalcode=" + postalcode +
			", city=" + city +
			", country=" + country +
			">>";
	}
	@SuppressWarnings("unchecked")
	static public class Builder<_T1,_T2,_T3,_T4,_T5> {
		private String street;
		private int houseNumber;
		@Nullable private String busNumber;
		private String postalcode;
		private String city;
		private String country;
		
		public Builder<SET,_T2,_T3,_T4,_T5> setStreet(String street) {
			this.street = street;
			return (Builder<SET,_T2,_T3,_T4,_T5>) this;
		}
		public Builder<_T1,SET,_T3,_T4,_T5> setHouseNumber(int houseNumber) {
			this.houseNumber = houseNumber;
			return (Builder<_T1,SET,_T3,_T4,_T5>) this;
		}
		public Builder<_T1,_T2,_T3,_T4,_T5> setBusNumber(String busNumber) {
			this.busNumber = busNumber;
			return (Builder<_T1,_T2,_T3,_T4,_T5>) this;
		}
		public Builder<_T1,_T2,SET,_T4,_T5> setPostalcode(String postalcode) {
			this.postalcode = postalcode;
			return (Builder<_T1,_T2,SET,_T4,_T5>) this;
		}
		public Builder<_T1,_T2,_T3,SET,_T5> setCity(String city) {
			this.city = city;
			return (Builder<_T1,_T2,_T3,SET,_T5>) this;
		}
		public Builder<_T1,_T2,_T3,_T4,SET> setCountry(String country) {
			this.country = country;
			return (Builder<_T1,_T2,_T3,_T4,SET>) this;
		}
	}
	static public  Address build(Function<Builder<NOT,NOT,NOT,NOT,NOT>,Builder<SET,SET,SET,SET,SET>> supplier) {
		Builder b = supplier.apply(new Builder<>());
		return new Address(b.street, b.houseNumber, b.busNumber, b.postalcode, b.city, b.country);
	}
}
