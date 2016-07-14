package com.persistentbit.sql.dbdef;

/**
 * @author Peter Muys
 * @since 14/07/2016
 */
public class TableColDef {
    private final String name;
    private final boolean isId;
    private final boolean isAutoGen;

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

    public String getName() {
        return name;
    }

    public boolean isId() {
        return isId;
    }

    public boolean isAutoGen() {
        return isAutoGen;
    }

    @Override
    public String toString() {
        String id= isId? ", id" : "";
        String autoGen = isAutoGen ? ", autogen" : "";
        return "Col[" + name + id + autoGen + "]";
    }
}
