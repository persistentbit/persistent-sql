package com.persistentbit.sql;

import com.persistentbit.core.codegen.CaseClass;
import com.persistentbit.core.codegen.GenNoLens;
import com.persistentbit.core.properties.FieldNames;
import com.persistentbit.sql.statement.annotations.DbRename;
import com.persistentbit.sql.statement.annotations.DbTableName;

/**
 * User: petermuys
 * Date: 17/07/16
 * Time: 11:35
 */
@CaseClass @GenNoLens
@DbTableName("INVOICE_LINE")
public class InvoiceLine {
    private final int id;
    @DbRename("invoice_id") private final int invoiceId;
    private final String product;

    @FieldNames(names = {"id","invoiceId","product"})
    public InvoiceLine(int id, int invoiceId, String product) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.product = product;

    }

    @Override
    public String toString() {
        return "InvoiceLine{" +
                "id=" + id +
                ", invoiceId=" + invoiceId +
                ", product='" + product + '\'' +
                '}';
    }
	//[[ImmutableCodeBuilder]]

	public InvoiceLine	 withId(int value){
		return new InvoiceLine(value, this.invoiceId, this.product);
	} 

	public int getId(){ return id; }

	public InvoiceLine	 withInvoiceId(int value){
		return new InvoiceLine(this.id, value, this.product);
	} 

	public int getInvoiceId(){ return invoiceId; }

	public InvoiceLine	 withProduct(String value){
		return new InvoiceLine(this.id, this.invoiceId, value);
	} 

	public String getProduct(){ return product; }

	@Override
	public boolean equals(Object o){
		if(o == this) { return true; }
		if(o instanceof InvoiceLine ==false) { return false; }
		InvoiceLine other = (InvoiceLine)o;
		if(invoiceId == other.invoiceId == false){ return false; }
		if(product.equals(other.product) == false){ return false; }
		if(id == other.id == false){ return false; }
		return true;
	}

	@Override
	public int hashCode(){
		int result=0;
		result = 31 * result + Integer.hashCode(invoiceId);
		result = 31 * result + product.hashCode();
		result = 31 * result + Integer.hashCode(id);
		return result;
	}

}
