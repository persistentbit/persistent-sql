package com.persistentbit.sql.databases;

/**
 * @author Peter Muys
 * @since 19/07/2016
 */
public class DbDerby extends AbstractDbType{
    public DbDerby() {
        super("Apache Derby");
    }

    @Override
    public String sqlWithLimit(long limit, String sql) {
        return sql + " FETCH NEXT " + limit + " ROWS ONLY ";
    }

    @Override
    public String sqlWithLimitAndOffset(long limit, long offset, String sql) {
        return sqlWithLimit(limit,sql + " OFFSET " + offset + " ROWS ");
    }

    static public String urlInMemory(String name) {
        return "jdbc:derby:memory:"+name+";create=true";
    }

    static public String url(String filePath){
        return "jdbc:derby:" + filePath.replace('\\','/') + ";create=true";
    }

    static public String getDriverClassName( ){
        return "org.apache.derby.jdbc.EmbeddedDriver";
    }

}
