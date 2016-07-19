package com.persistentbit.sql.databases;

/**
 * @author Peter Muys
 * @since 19/07/2016
 */
public class DbPostgress extends AbstractDbType{
    public DbPostgress(){
        super("PostgreSQL");
    }
    @Override
    public String sqlWithLimit(int limit, String sql) {
        return sql + " LIMIT "+ limit;
    }

    @Override
    public String sqlWithLimitAndOffset(int limit, int offset, String sql) {
        return sql + " LIMIT " + limit + " OFFSET " + offset;
    }


    static String connectionUrlLocal(String db){
        return connectionUrl("localhost",db);
    }

    static String connectionUrl(String host,String db){
        return connectionUrl(host,5432,db);
    }

    static String connectionUrl(String host,int port, String db){
        return "jdbc:postgresql://"+host+ ":" + port + "/" + db;
    }

    static String getDriverClassName( ){
        return "org.postgresql.Driver";
    }
}
