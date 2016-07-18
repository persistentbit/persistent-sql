package com.persistentbit.sql;

import com.persistentbit.core.codegen.CaseClass;
import com.persistentbit.core.codegen.GenNoLens;
import com.persistentbit.core.properties.FieldNames;
import com.persistentbit.sql.references.LongRef;
import com.persistentbit.sql.references.LongRefValue;

/**
 * User: petermuys
 * Date: 15/07/16
 * Time: 19:28
 */
@CaseClass @GenNoLens
public class Person {
    private final long id;
    private final String userName;
    private final String password;

    @FieldNames(names={"id","userName","password"})
    public Person(long id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    public LongRef<Person> getRef() {
    	return new LongRefValue(this.getId(),this);
	}

    public Person withName(String name){
        return new Person(id,name,password);
    }

    @Override
    public String toString() {
        return "TestRecord{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
	//[[ImmutableCodeBuilder]]

	public Person	 withId(long value){
		return new Person(value, this.userName, this.password);
	} 

	public long getId(){ return id; }

	
	@Override
	public boolean equals(Object o){
		if(o == this) { return true; }
		if(o instanceof Person ==false) { return false; }
		Person other = (Person)o;
		if(userName.equals(other.userName) == false){ return false; }
		if(id == other.id == false){ return false; }
		if(password.equals(other.password) == false){ return false; }
		return true;
	}

	@Override
	public int hashCode(){
		int result=0;
		result = 31 * result + userName.hashCode();
		result = 31 * result + Long.hashCode(id);
		result = 31 * result + password.hashCode();
		return result;
	}

	//[[ImmutableCodeBuilder]]

	public Person	 withUserName(String value){
		return new Person(this.id, value, this.password);
	} 

	public String getUserName(){ return userName; }

	public Person	 withPassword(String value){
		return new Person(this.id, this.userName, value);
	} 

	public String getPassword(){ return password; }

}
