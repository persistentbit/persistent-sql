// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-15T11:05:17.091

package com.persistentbit.sql.test;

import com.persistentbit.core.utils.builders.SET;
import com.persistentbit.core.utils.builders.NOT;
import java.util.function.Function;
import java.util.Optional;
import com.persistentbit.core.Nullable;
import java.util.Objects;

/**
 * 
 * This immutable value class contains the data for a record in the table 'PERSON'.<br>
 * Generated from the database on 2017-01-15T11:04:40.734<br>
 * 
 */
public class SPerson {
	private final int id;
	private final String userName;
	private final String password;
	private final String street;
	private final int houseNumber;
	@Nullable private final String busNumber;
	private final String postalcode;
	private final String city;
	private final String country;
	
	public SPerson(int id, String userName, String password, String street, int houseNumber, String busNumber, String postalcode, String city, String country) {
		this.id = id;
		this.userName = Objects.requireNonNull(userName,"userName in SPerson can't be null");
		this.password = Objects.requireNonNull(password,"password in SPerson can't be null");
		this.street = Objects.requireNonNull(street,"street in SPerson can't be null");
		this.houseNumber = houseNumber;
		this.busNumber = busNumber;
		this.postalcode = Objects.requireNonNull(postalcode,"postalcode in SPerson can't be null");
		this.city = Objects.requireNonNull(city,"city in SPerson can't be null");
		this.country = Objects.requireNonNull(country,"country in SPerson can't be null");
	}
	public SPerson(int id, String userName, String password, String street, int houseNumber, String postalcode, String city, String country) {
		this(id,userName,password,street,houseNumber,null,postalcode,city,country);
	}
	public int getId() { return id; }
	public SPerson withId(int id) { return new SPerson(id, this.userName, this.password, this.street, this.houseNumber, this.busNumber, this.postalcode, this.city, this.country); }
	
	public String getUserName() { return userName; }
	public SPerson withUserName(String userName) { return new SPerson(this.id, userName, this.password, this.street, this.houseNumber, this.busNumber, this.postalcode, this.city, this.country); }
	
	public String getPassword() { return password; }
	public SPerson withPassword(String password) { return new SPerson(this.id, this.userName, password, this.street, this.houseNumber, this.busNumber, this.postalcode, this.city, this.country); }
	
	public String getStreet() { return street; }
	public SPerson withStreet(String street) { return new SPerson(this.id, this.userName, this.password, street, this.houseNumber, this.busNumber, this.postalcode, this.city, this.country); }
	
	public int getHouseNumber() { return houseNumber; }
	public SPerson withHouseNumber(int houseNumber) { return new SPerson(this.id, this.userName, this.password, this.street, houseNumber, this.busNumber, this.postalcode, this.city, this.country); }
	
	public Optional<String> getBusNumber() { return Optional.ofNullable(busNumber); }
	public SPerson withBusNumber(String busNumber) { return new SPerson(this.id, this.userName, this.password, this.street, this.houseNumber, busNumber, this.postalcode, this.city, this.country); }
	
	public String getPostalcode() { return postalcode; }
	public SPerson withPostalcode(String postalcode) { return new SPerson(this.id, this.userName, this.password, this.street, this.houseNumber, this.busNumber, postalcode, this.city, this.country); }
	
	public String getCity() { return city; }
	public SPerson withCity(String city) { return new SPerson(this.id, this.userName, this.password, this.street, this.houseNumber, this.busNumber, this.postalcode, city, this.country); }
	
	public String getCountry() { return country; }
	public SPerson withCountry(String country) { return new SPerson(this.id, this.userName, this.password, this.street, this.houseNumber, this.busNumber, this.postalcode, this.city, country); }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		SPerson that = (SPerson)o;
		
		if(id != that.id) return false;
		if(!userName.equals(that.userName)) return false;
		if(!password.equals(that.password)) return false;
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
		result = Integer.hashCode(id);
		result = 31 * result + userName.hashCode();
		result = 31 * result + password.hashCode();
		result = 31 * result + street.hashCode();
		result = 31 * result + Integer.hashCode(houseNumber);
		result = 31 * result + (busNumber != null ? busNumber.hashCode(): 0);
		result = 31 * result + postalcode.hashCode();
		result = 31 * result + city.hashCode();
		result = 31 * result + country.hashCode();
		return result;
	}
	@Override
	public String toString() {
		return "SPerson<<" +
			"id=" + id +
			", userName=" + userName +
			", password=" + password +
			", street=" + street +
			", houseNumber=" + houseNumber +
			(busNumber == null ? "" : ", busNumber=" + busNumber) +
			", postalcode=" + postalcode +
			", city=" + city +
			", country=" + country +
			">>";
	}
	
	static public class Builder<_T1,_T2,_T3,_T4,_T5,_T6,_T7,_T8> {
		private int id;
		private String userName;
		private String password;
		private String street;
		private int houseNumber;
		@Nullable private String busNumber;
		private String postalcode;
		private String city;
		private String country;
		
		public Builder<SET,_T2,_T3,_T4,_T5,_T6,_T7,_T8> setId(int id) {
			this.id = id;
			return (Builder<SET,_T2,_T3,_T4,_T5,_T6,_T7,_T8>) this;
		}
		public Builder<_T1,SET,_T3,_T4,_T5,_T6,_T7,_T8> setUserName(String userName) {
			this.userName = userName;
			return (Builder<_T1,SET,_T3,_T4,_T5,_T6,_T7,_T8>) this;
		}
		public Builder<_T1,_T2,SET,_T4,_T5,_T6,_T7,_T8> setPassword(String password) {
			this.password = password;
			return (Builder<_T1,_T2,SET,_T4,_T5,_T6,_T7,_T8>) this;
		}
		public Builder<_T1,_T2,_T3,SET,_T5,_T6,_T7,_T8> setStreet(String street) {
			this.street = street;
			return (Builder<_T1,_T2,_T3,SET,_T5,_T6,_T7,_T8>) this;
		}
		public Builder<_T1,_T2,_T3,_T4,SET,_T6,_T7,_T8> setHouseNumber(int houseNumber) {
			this.houseNumber = houseNumber;
			return (Builder<_T1,_T2,_T3,_T4,SET,_T6,_T7,_T8>) this;
		}
		public Builder<_T1,_T2,_T3,_T4,_T5,_T6,_T7,_T8> setBusNumber(String busNumber) {
			this.busNumber = busNumber;
			return (Builder<_T1,_T2,_T3,_T4,_T5,_T6,_T7,_T8>) this;
		}
		public Builder<_T1,_T2,_T3,_T4,_T5,SET,_T7,_T8> setPostalcode(String postalcode) {
			this.postalcode = postalcode;
			return (Builder<_T1,_T2,_T3,_T4,_T5,SET,_T7,_T8>) this;
		}
		public Builder<_T1,_T2,_T3,_T4,_T5,_T6,SET,_T8> setCity(String city) {
			this.city = city;
			return (Builder<_T1,_T2,_T3,_T4,_T5,_T6,SET,_T8>) this;
		}
		public Builder<_T1,_T2,_T3,_T4,_T5,_T6,_T7,SET> setCountry(String country) {
			this.country = country;
			return (Builder<_T1,_T2,_T3,_T4,_T5,_T6,_T7,SET>) this;
		}
	}
	static public  SPerson build(Function<Builder<NOT,NOT,NOT,NOT,NOT,NOT,NOT,NOT>,Builder<SET,SET,SET,SET,SET,SET,SET,SET>> supplier) {
		Builder b = supplier.apply(new Builder<>());
		return new SPerson(b.id, b.userName, b.password, b.street, b.houseNumber, b.busNumber, b.postalcode, b.city, b.country);
	}
}
