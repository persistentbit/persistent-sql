package com.persistentbit.sql;

import com.persistentbit.core.codegen.CaseClass;
import com.persistentbit.core.lenses.Lens;
import com.persistentbit.core.lenses.LensImpl;
import com.persistentbit.core.properties.FieldNames;

/**
 * User: petermuys
 * Date: 17/07/16
 * Time: 11:25
 */
@CaseClass
public class Invoice {
    private final int id;
    private final String number;
    private int fromPersonId;
    private int toPersonId;

    @FieldNames(names = {"id","number","fromPersonId","toPersonId"})
    public Invoice(int id, String number, int fromPersonId, int toPersonId) {
        this.id = id;
        this.number = number;
        this.fromPersonId = fromPersonId;
        this.toPersonId = toPersonId;
    }


	//[[ImmutableCodeBuilder]]

	public Invoice	 withId(int value){
		return new Invoice(value, this.number, this.fromPersonId, this.toPersonId);
	} 

	public int getId(){ return id; }

	static public final Lens<Invoice,Integer> _id = new LensImpl<Invoice,Integer>(obj-> obj.getId(),(obj, value)-> obj.withId(value));

	public Invoice	 withNumber(String value){
		return new Invoice(this.id, value, this.fromPersonId, this.toPersonId);
	} 

	public String getNumber(){ return number; }

	static public final Lens<Invoice,String> _number = new LensImpl<Invoice,String>(obj-> obj.getNumber(),(obj, value)-> obj.withNumber(value));

	@Override
	public boolean equals(Object o){
		if(o == this) { return true; }
		if(o instanceof Invoice ==false) { return false; }
		Invoice other = (Invoice)o;
		if(number.equals(other.number) == false){ return false; }
		if(toPersonId == other.toPersonId == false){ return false; }
		if(fromPersonId == other.fromPersonId == false){ return false; }
		if(id == other.id == false){ return false; }
		return true;
	}

	@Override
	public int hashCode(){
		int result=0;
		result = 31 * result + number.hashCode();
		result = 31 * result + Integer.hashCode(toPersonId);
		result = 31 * result + Integer.hashCode(fromPersonId);
		result = 31 * result + Integer.hashCode(id);
		return result;
	}

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", fromPersonId=" + fromPersonId +
                ", toPersonId=" + toPersonId +
                '}';
    }

}
