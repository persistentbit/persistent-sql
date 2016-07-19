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
    public String sqlWithLimit(long limit, String sql) {
        return sql + " LIMIT "+ limit;
    }

    @Override
    public String sqlWithLimitAndOffset(long limit, long offset, String sql) {
        return sql + " LIMIT " + limit + " OFFSET " + offset;
    }


    static public String connectionUrlLocal(String db){
        return connectionUrl("localhost",db);
    }

    static public String connectionUrl(String host,String db){
        return connectionUrl(host,5432,db);
    }

    static public String connectionUrl(String host,int port, String db){
        return "jdbc:postgresql://"+host+ ":" + port + "/" + db;
    }

    static public String getDriverClassName( ){
        return "org.postgresql.Driver";
    }
}
