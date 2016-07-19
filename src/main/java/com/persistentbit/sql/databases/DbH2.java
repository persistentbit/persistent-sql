package com.persistentbit.sql.databases;

/**
 * @author Peter Muys
 * @since 19/07/2016
 */
public class DbH2 extends AbstractDbType{
    public DbH2(){
        super("H2");
    }
    @Override
    public String sqlWithLimit(int limit, String sql) {
        return sql + " LIMIT "+ limit;
    }

    @Override
    public String sqlWithLimitAndOffset(int limit, int offset, String sql) {
        return sql + " LIMIT " + limit + " OFFSET " + offset;
    }
}
