package com.persistentbit.sql.databases;

/**
 * @author Peter Muys
 * @since 19/07/2016
 */
public class DbMySql extends AbstractDbType{

    public DbMySql() {
        super("MySQL");
    }

    @Override
    public String sqlWithLimit(int limit, String sql) {
        return sql + " LIMIT "+ limit;
    }

    @Override
    public String sqlWithLimitAndOffset(int limit, int offset, String sql) {
        return sql + " LIMIT " + limit + " OFFSET " + offset;
    }

    static String connectionUrl(String host, String db){
        return connectionUrl(host,3306,db);
    }
    static String connectionUrl(String host,int port, String db){
        return "jdbc:mysql://"+host+ ":" + port + "/" + db;
    }

    static String getDriverClassName( ){
        return "com.mysql.jdbc.Driver";
    }

}
