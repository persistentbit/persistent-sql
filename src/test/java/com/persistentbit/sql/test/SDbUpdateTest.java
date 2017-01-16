// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.022

package com.persistentbit.sql.test;

import com.persistentbit.core.Nullable;
import com.persistentbit.core.utils.builders.NOT;
import com.persistentbit.core.utils.builders.SET;

import java.util.Optional;
import java.util.function.Function;

/**
 * 
 * This immutable value class contains the data for a record in the table 'DB_UPDATE_TEST'.<br>
 * Generated from the database on 2017-01-15T11:05:17.006<br>
 * 
 */
public class SDbUpdateTest {
	private final int id;
	@Nullable private final String name;
	
	public SDbUpdateTest(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public SDbUpdateTest(int id) {
		this(id,null);
	}
	public int getId() { return id; }
	public SDbUpdateTest withId(int id) { return new SDbUpdateTest(id, this.name); }
	
	public Optional<String> getName() { return Optional.ofNullable(name); }
	public SDbUpdateTest withName(String name) { return new SDbUpdateTest(this.id, name); }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		SDbUpdateTest that = (SDbUpdateTest)o;
		
		if(id != that.id) return false;
		if(name!= null ? !name.equals(that.name) : that.name != null) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int result;
		result = Integer.hashCode(id);
		result = 31 * result + (name != null ? name.hashCode(): 0);
		return result;
	}
	@Override
	public String toString() {
		return "SDbUpdateTest<<" +
			"id=" + id +
			(name == null ? "" : ", name=" + name) +
			">>";
	}
	
	static public class Builder<_T1> {
		private int id;
		@Nullable private String name;
		
		public Builder<SET> setId(int id) {
			this.id = id;
			return (Builder<SET>) this;
		}
		public Builder<_T1> setName(String name) {
			this.name = name;
			return (Builder<_T1>) this;
		}
	}
	static public  SDbUpdateTest build(Function<Builder<NOT>,Builder<SET>> supplier) {
		Builder b = supplier.apply(new Builder<>());
		return new SDbUpdateTest(b.id, b.name);
	}
}
