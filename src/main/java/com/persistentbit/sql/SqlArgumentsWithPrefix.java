package com.persistentbit.sql;

/**
 * @author Peter Muys
 * @since 4/07/2016
 */
public class SqlArgumentsWithPrefix implements SqlArguments<SqlArgumentsWithPrefix>{
    private SqlArguments<?> master;
    private String prefix;

    public SqlArgumentsWithPrefix(String prefix, SqlArguments<?> master){
        this.master = master;
        this.prefix = prefix;
    }
    @Override
    public SqlArgumentsWithPrefix arg(String name, Object value) {
        master.arg(prefix+name,value);
        return this;
    }
}
