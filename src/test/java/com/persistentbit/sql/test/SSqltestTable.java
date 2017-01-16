// WARNING !
// GENERATED CODE FOR SUBSTEMA PACKAGE com.persistentbit.sql.test
// See resource file com.persistentbit.sql.test.substema for the definition.
// generated on 2017-01-16T11:33:03.044

package com.persistentbit.sql.test;

import com.persistentbit.core.utils.builders.NOT;
import com.persistentbit.core.utils.builders.SET;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Function;

/**
 * 
 * This immutable value class contains the data for a record in the table 'SQLTEST_TABLE'.<br>
 * Generated from the database on 2017-01-15T11:05:17.025<br>
 * 
 */
public class SSqltestTable {
	private final int id;
	private final LocalDateTime createdDate;
	private final String moduleName;
	private final String className;
	private final String methodName;
	
	public SSqltestTable(int id, LocalDateTime createdDate, String moduleName, String className, String methodName) {
		this.id = id;
		this.createdDate = Objects.requireNonNull(createdDate,"createdDate in SSqltestTable can't be null");
		this.moduleName = Objects.requireNonNull(moduleName,"moduleName in SSqltestTable can't be null");
		this.className = Objects.requireNonNull(className,"className in SSqltestTable can't be null");
		this.methodName = Objects.requireNonNull(methodName,"methodName in SSqltestTable can't be null");
	}
	public int getId() { return id; }
	public SSqltestTable withId(int id) { return new SSqltestTable(id, this.createdDate, this.moduleName, this.className, this.methodName); }
	
	public LocalDateTime getCreatedDate() { return createdDate; }
	public SSqltestTable withCreatedDate(LocalDateTime createdDate) { return new SSqltestTable(this.id, createdDate, this.moduleName, this.className, this.methodName); }
	
	public String getModuleName() { return moduleName; }
	public SSqltestTable withModuleName(String moduleName) { return new SSqltestTable(this.id, this.createdDate, moduleName, this.className, this.methodName); }
	
	public String getClassName() { return className; }
	public SSqltestTable withClassName(String className) { return new SSqltestTable(this.id, this.createdDate, this.moduleName, className, this.methodName); }
	
	public String getMethodName() { return methodName; }
	public SSqltestTable withMethodName(String methodName) { return new SSqltestTable(this.id, this.createdDate, this.moduleName, this.className, methodName); }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		SSqltestTable that = (SSqltestTable)o;
		
		if(id != that.id) return false;
		if(!createdDate.equals(that.createdDate)) return false;
		if(!moduleName.equals(that.moduleName)) return false;
		if(!className.equals(that.className)) return false;
		if(!methodName.equals(that.methodName)) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int result;
		result = Integer.hashCode(id);
		result = 31 * result + createdDate.hashCode();
		result = 31 * result + moduleName.hashCode();
		result = 31 * result + className.hashCode();
		result = 31 * result + methodName.hashCode();
		return result;
	}
	@Override
	public String toString() {
		return "SSqltestTable<<" +
			"id=" + id +
			", createdDate=" + createdDate +
			", moduleName=" + moduleName +
			", className=" + className +
			", methodName=" + methodName +
			">>";
	}
	
	static public class Builder<_T1,_T2,_T3,_T4,_T5> {
		private int id;
		private LocalDateTime createdDate;
		private String moduleName;
		private String className;
		private String methodName;
		
		public Builder<SET,_T2,_T3,_T4,_T5> setId(int id) {
			this.id = id;
			return (Builder<SET,_T2,_T3,_T4,_T5>) this;
		}
		public Builder<_T1,SET,_T3,_T4,_T5> setCreatedDate(LocalDateTime createdDate) {
			this.createdDate = createdDate;
			return (Builder<_T1,SET,_T3,_T4,_T5>) this;
		}
		public Builder<_T1,_T2,SET,_T4,_T5> setModuleName(String moduleName) {
			this.moduleName = moduleName;
			return (Builder<_T1,_T2,SET,_T4,_T5>) this;
		}
		public Builder<_T1,_T2,_T3,SET,_T5> setClassName(String className) {
			this.className = className;
			return (Builder<_T1,_T2,_T3,SET,_T5>) this;
		}
		public Builder<_T1,_T2,_T3,_T4,SET> setMethodName(String methodName) {
			this.methodName = methodName;
			return (Builder<_T1,_T2,_T3,_T4,SET>) this;
		}
	}
	static public  SSqltestTable build(Function<Builder<NOT,NOT,NOT,NOT,NOT>,Builder<SET,SET,SET,SET,SET>> supplier) {
		Builder b = supplier.apply(new Builder<>());
		return new SSqltestTable(b.id, b.createdDate, b.moduleName, b.className, b.methodName);
	}
}
