package com.persistentbit.sql.databases;

import com.persistentbit.core.Tuple2;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.PersistSqlException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author Peter Muys
 * @since 19/07/2016
 */
public class DbTypeRegistry {
    static private final Logger log = Logger.getLogger(DbTypeRegistry.class.getName());
    private PMap<String,DbType> reg = PMap.empty();


    public DbTypeRegistry() {
        register(new DbDerby(),new DbH2(),new DbMySql(),new DbPostgress());
    }

    public DbTypeRegistry register(DbType...types){
        reg = reg.plusAll(PStream.from(types).map(t -> Tuple2.of(t.getDatabaseName(),t)));
        return this;
    }
    public Optional<DbType>   getDbType(Connection c){
        try {
            return getDbType(c.getMetaData());
        } catch (SQLException e) {
           throw new PersistSqlException(e);
        }
    }

    public Optional<DbType> getDbType(DatabaseMetaData md){
        try {
            DbType res = reg.get(md.getDatabaseProductName());
            if(res == null){
                log.warning("Can't find Db Type for database with name '"+ md.getDatabaseProductName() +"'");
            }
            return Optional.ofNullable(res);
        } catch (SQLException e) {
            throw new PersistSqlException(e);
        }
    }

}