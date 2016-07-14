package com.persistentbit.sql.dbdef;

import com.persistentbit.sql.connect.SQLRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A table definition supplier using jdbc meta data<br>
 * A the table definitions are cached.<br>
 * @author Peter Muys
 * @since 14/07/2016
 */
public class TableDefSupplierImpl implements Function<String,TableDef> {
    private final SQLRunner runner;
    private Map<String,TableDef>    defs = Collections.synchronizedMap(new  HashMap<>());


    public TableDefSupplierImpl(SQLRunner runner){
        this.runner = runner;
    }

    public TableDef apply(String name){
        TableDef res = defs.getOrDefault(name.toLowerCase(),null);
        if(res == null){
            res = runner.run((c) -> {
                TableDef r =  TableDef.getFromDb(name,c);
                defs.put(name.toLowerCase(),r);
                return r;
            });
        }
        return res;
    }


}
