package com.persistentbit.sql;

import com.persistentbit.core.codegen.CaseClass;
import com.persistentbit.core.codegen.GenNoLens;
import com.persistentbit.core.properties.FieldNames;
import com.persistentbit.core.utils.ImTools;
import com.persistentbit.sql.references.Ref;
import com.persistentbit.sql.references.RefId;
import com.persistentbit.sql.references.RefValue;
import com.persistentbit.sql.statement.annotations.DbRename;
import com.persistentbit.sql.statement.annotations.DbTableName;

/**
 * User: petermuys
 * Date: 15/07/16
 * Time: 19:28
 */
@CaseClass @GenNoLens
@DbTableName("PERSON")
public class Person {
    private final long id;
    @DbRename("user_name")
	private final String userName;
    private final String password;

    @FieldNames(names={"id","userName","password"})
    public Person(long id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    public Ref<Person,Long> getRef() {
    	return new RefValue(new RefId(getId()),this);
	}

    public Person withName(String name){
        return new Person(id,name,password);
    }

    @Override
    public String toString() {
        return "Person{" +
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
