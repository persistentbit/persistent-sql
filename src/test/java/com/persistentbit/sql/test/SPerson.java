// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.032

package com.persistentbit.sql.test;

import com.persistentbit.core.utils.builders.NOT;
import com.persistentbit.core.utils.builders.SET;

import java.util.Objects;
import java.util.function.Function;

/**
 * 
 * This immutable value class contains the data for a record in the table 'PERSON'.<br>
 * Generated from the database on 2017-01-15T11:05:17.019<br>
 * 
 */
public class SPerson {
	private final int     id;
	private final String  userName;
	private final String  password;
	private final Address address;

	public SPerson(int id, String userName, String password, Address address) {
		this.id = id;
		this.userName = Objects.requireNonNull(userName,"userName in SPerson can't be null");
		this.password = Objects.requireNonNull(password,"password in SPerson can't be null");
		this.address = Objects.requireNonNull(address, "address in SPerson can't be null");
	}
	public int getId() { return id; }

	public SPerson withId(int id) { return new SPerson(id, this.userName, this.password, this.address); }
	
	public String getUserName() { return userName; }

	public SPerson withUserName(String userName) { return new SPerson(this.id, userName, this.password, this.address); }
	
	public String getPassword() { return password; }

	public SPerson withPassword(String password) { return new SPerson(this.id, this.userName, password, this.address); }

	public Address getAddress() { return address; }

	public SPerson withAddress(Address address) { return new SPerson(this.id, this.userName, this.password, address); }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		SPerson that = (SPerson)o;
		
		if(id != that.id) return false;
		if(!userName.equals(that.userName)) return false;
		if(!password.equals(that.password)) return false;
		if(!address.equals(that.address)) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int result;
		result = Integer.hashCode(id);
		result = 31 * result + userName.hashCode();
		result = 31 * result + password.hashCode();
		result = 31 * result + address.hashCode();
		return result;
	}
	@Override
	public String toString() {
		return "SPerson<<" +
			"id=" + id +
			", userName=" + userName +
			", password=" + password +
			", address=" + address +
			">>";
	}

	static public class Builder<_T1, _T2, _T3, _T4>{
		private int     id;
		private String  userName;
		private String  password;
		private Address address;

		public Builder<SET, _T2, _T3, _T4> setId(int id) {
			this.id = id;
			return (Builder<SET, _T2, _T3, _T4>) this;
		}

		public Builder<_T1, SET, _T3, _T4> setUserName(String userName) {
			this.userName = userName;
			return (Builder<_T1, SET, _T3, _T4>) this;
		}

		public Builder<_T1, _T2, SET, _T4> setPassword(String password) {
			this.password = password;
			return (Builder<_T1, _T2, SET, _T4>) this;
		}

		public Builder<_T1, _T2, _T3, SET> setAddress(Address address) {
			this.address = address;
			return (Builder<_T1, _T2, _T3, SET>) this;
		}
	}

	static public SPerson build(Function<Builder<NOT, NOT, NOT, NOT>, Builder<SET, SET, SET, SET>> supplier) {
		Builder b = supplier.apply(new Builder<>());
		return new SPerson(b.id, b.userName, b.password, b.address);
	}
}
