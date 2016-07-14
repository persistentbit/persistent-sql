package com.persistentbit.sql;

/**
 * @author Peter Muys
 * @since 14/07/2016
 */
public class TableColDef {
    final String name;
    final boolean isId;
    final boolean isAutoGen;
    public TableColDef(String name){
        this(name,false,false);
    }
    public TableColDef(String name, boolean isId, boolean isAutoGen)
    {
        this.name = name;
        this.isAutoGen = isAutoGen;
        this.isId = isId;
    }
    public TableColDef asId(){
        return new TableColDef(name,true,isAutoGen);
    }
    public TableColDef asAutoGen() {
        return new TableColDef(name,true,true);
    }

    @Override
    public String toString() {
        String id= isId? ", id" : "";
        String autoGen = isAutoGen ? ", autogen" : "";
        return "Col[" + name + id + autoGen + "]";
    }
}
