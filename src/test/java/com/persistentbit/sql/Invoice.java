package com.persistentbit.sql;

import com.persistentbit.core.codegen.CaseClass;
import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.core.lenses.Lens;
import com.persistentbit.core.lenses.LensImpl;
import com.persistentbit.core.properties.FieldNames;

import com.persistentbit.core.utils.ImTools;
import com.persistentbit.sql.objectmappers.InMemoryRow;
import com.persistentbit.sql.objectmappers.ObjectRowMapper;
import com.persistentbit.sql.references.Ref;
import com.persistentbit.sql.references.RefId;
import com.persistentbit.sql.statement.annotations.DbIgnore;
import com.persistentbit.sql.statement.annotations.DbRename;
import com.persistentbit.sql.statement.annotations.DbTableName;

import java.lang.reflect.Type;

/**
 * User: petermuys
 * Date: 17/07/16
 * Time: 11:25
 */
@CaseClass @DbTableName("INVOICE")
public class Invoice {
    private final int id;
    @DbRename("invoice_nummer") private final String number;
    @DbRename("from_person_id")private Ref<Person,Long> fromPersonId;
	@DbRename("to_person_id")private Ref<Person,Long> toPersonId;
	@DbIgnore private PStream<InvoiceLine> lines;


	public Invoice(String number, Ref<Person,Long> fromPersonId, Ref<Person,Long> toPersonId){
    	this(0,number,fromPersonId,toPersonId,PList.empty());
	}
	@FieldNames(names = {"id","number","fromPersonId","toPersonId","lines"})
    public Invoice(int id, String number, Ref<Person,Long> fromPersonId, Ref<Person,Long> toPersonId,PStream<InvoiceLine> lines) {
        this.id = id;
        this.number = number;
        this.fromPersonId = fromPersonId;
        this.toPersonId = toPersonId;
		this.lines = lines == null ? PList.empty() : lines;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", fromPersonId=" + fromPersonId +
                ", toPersonId=" + toPersonId +
				", lines= " + lines +
                '}';
    }

	public Ref<Person, Long> getFromPersonId() {
		return fromPersonId;
	}

	public Ref<Person, Long> getToPersonId() {
		return toPersonId;
	}


	//[[ImmutableCodeBuilder]]

	public Invoice	 withId(int value){
		return new Invoice(value, this.number, this.fromPersonId, this.toPersonId, this.lines);
	} 

	public int getId(){ return id; }

	static public final Lens<Invoice,Integer> _id = new LensImpl<Invoice,Integer>(obj-> obj.getId(),(obj,value)-> obj.withId(value));

	public Invoice	 withNumber(String value){
		return new Invoice(this.id, value, this.fromPersonId, this.toPersonId, this.lines);
	} 

	public String getNumber(){ return number; }

	public Invoice withLines(PStream<InvoiceLine> lines){
		return new Invoice(this.id, this.number, this.fromPersonId, this.toPersonId, lines);
	}

	public Invoice withFromPerson(Ref<Person,Long> p){
		return new Invoice(this.id, this.number, p, this.toPersonId, this.lines);
	}

	public Invoice withToPerson(Ref<Person,Long> p){
		return new Invoice(this.id, this.number, this.fromPersonId, p, this.lines);
	}

	public PStream<InvoiceLine> getLines() {
		return lines;
	}

	static public final Lens<Invoice,String> _number = new LensImpl<Invoice,String>(obj-> obj.getNumber(),(obj, value)-> obj.withNumber(value));

	@Override
	public boolean equals(Object o){
		if(o == this) { return true; }
		if(o instanceof Invoice ==false) { return false; }
		Invoice other = (Invoice)o;
		if(number.equals(other.number) == false){ return false; }
		if(toPersonId.equals(other.toPersonId) == false){ return false; }
		if(fromPersonId.equals(other.fromPersonId) == false){ return false; }
		if(id == other.id == false){ return false; }
		if(lines.equals(other.lines) == false){ return false; }
		return true;
	}

	@Override
	public int hashCode(){
		int result=0;
		result = 31 * result + number.hashCode();
		result = 31 * result + toPersonId.hashCode();
		result = 31 * result + fromPersonId.hashCode();
		result = 31 * result + Integer.hashCode(id);
		result = 31 * result + lines.hashCode();
		return result;
	}
	static public void main(String...args){
		/*ImTools<Invoice> im = ImTools.get(Invoice.class);
		ImTools.Getter getter = im.getFieldGetters().find(g -> g.propertyName.equals("fromPersonId")).get();
		Type t = getter.field.getGenericType();*/

		Invoice in = new Invoice("1234",new RefId(100l),null);
		System.out.println(in);
		InMemoryRow row = new InMemoryRow() ;
		ObjectRowMapper mapper = new ObjectRowMapper();
		mapper.write(in,row);
		System.out.println(row);
		in = mapper.read(Invoice.class,row);
		System.out.println(in);


	}
}
