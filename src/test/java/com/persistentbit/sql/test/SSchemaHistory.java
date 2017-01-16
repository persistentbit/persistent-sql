// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.037

package com.persistentbit.sql.test;

import com.persistentbit.core.utils.builders.NOT;
import com.persistentbit.core.utils.builders.SET;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Function;

/**
 * 
 * This immutable value class contains the data for a record in the table 'SCHEMA_HISTORY'.<br>
 * Generated from the database on 2017-01-15T11:05:17.022<br>
 * 
 */
public class SSchemaHistory {
	private final LocalDateTime createddate;
	private final String packageName;
	private final String updateName;
	
	public SSchemaHistory(LocalDateTime createddate, String packageName, String updateName) {
		this.createddate = Objects.requireNonNull(createddate,"createddate in SSchemaHistory can't be null");
		this.packageName = Objects.requireNonNull(packageName,"packageName in SSchemaHistory can't be null");
		this.updateName = Objects.requireNonNull(updateName,"updateName in SSchemaHistory can't be null");
	}
	public LocalDateTime getCreateddate() { return createddate; }
	public SSchemaHistory withCreateddate(LocalDateTime createddate) { return new SSchemaHistory(createddate, this.packageName, this.updateName); }
	
	public String getPackageName() { return packageName; }
	public SSchemaHistory withPackageName(String packageName) { return new SSchemaHistory(this.createddate, packageName, this.updateName); }
	
	public String getUpdateName() { return updateName; }
	public SSchemaHistory withUpdateName(String updateName) { return new SSchemaHistory(this.createddate, this.packageName, updateName); }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		SSchemaHistory that = (SSchemaHistory)o;
		
		if(!createddate.equals(that.createddate)) return false;
		if(!packageName.equals(that.packageName)) return false;
		if(!updateName.equals(that.updateName)) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int result;
		result = createddate.hashCode();
		result = 31 * result + packageName.hashCode();
		result = 31 * result + updateName.hashCode();
		return result;
	}
	@Override
	public String toString() {
		return "SSchemaHistory<<" +
			"createddate=" + createddate +
			", packageName=" + packageName +
			", updateName=" + updateName +
			">>";
	}
	
	static public class Builder<_T1,_T2,_T3> {
		private LocalDateTime createddate;
		private String packageName;
		private String updateName;
		
		public Builder<SET,_T2,_T3> setCreateddate(LocalDateTime createddate) {
			this.createddate = createddate;
			return (Builder<SET,_T2,_T3>) this;
		}
		public Builder<_T1,SET,_T3> setPackageName(String packageName) {
			this.packageName = packageName;
			return (Builder<_T1,SET,_T3>) this;
		}
		public Builder<_T1,_T2,SET> setUpdateName(String updateName) {
			this.updateName = updateName;
			return (Builder<_T1,_T2,SET>) this;
		}
	}
	static public  SSchemaHistory build(Function<Builder<NOT,NOT,NOT>,Builder<SET,SET,SET>> supplier) {
		Builder b = supplier.apply(new Builder<>());
		return new SSchemaHistory(b.createddate, b.packageName, b.updateName);
	}
}
