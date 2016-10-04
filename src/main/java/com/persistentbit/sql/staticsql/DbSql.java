package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.function.Function2;
import com.persistentbit.core.logging.PLog;
import com.persistentbit.core.utils.NotYet;
import com.persistentbit.sql.databases.DbType;
import com.persistentbit.sql.staticsql.expr.ETypeObject;
import com.persistentbit.sql.staticsql.expr.Expr;
import com.persistentbit.sql.transactions.SQLTransactionRunner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by petermuys on 3/10/16.
 */
public class DbSql {
    static private final PLog log = PLog.get(DbSql.class);
    private final DbType    dbType;
    private final SQLTransactionRunner  trans;

    public DbSql(DbType dbType, SQLTransactionRunner trans) {
        this.dbType = dbType;
        this.trans = trans;
    }

    public Query    queryFrom(ETypeObject typeObject){
        return Query.from(this,typeObject);
    }

    public int runInsert(ETypeObject table, Expr...values){
        Insert insert = Insert.into(table,values);
        return run(insert);
    }

    public <T,K,R> R runInsertWithGenKeys(ETypeObject<T> table, T value, Expr<K> generatedKey,Function2<T,K,R> mapper){
        K key = runInsertWithGenKeys(table,value, generatedKey);
        return mapper.apply(value,key);
    }
    public <T,K> K runInsertWithGenKeys(ETypeObject<T> table, T value, Expr<K> generatedKey){
        return run(Insert.into(table,table.val(value)).withGeneratedKeys(generatedKey));
    }

    public int run(Insert insert){
        InsertSqlBuilder b = new InsertSqlBuilder(dbType,insert);
        String sql = b.generate();
        log.debug(sql);
        return trans.run(c -> {
            PreparedStatement s = c.prepareStatement(sql);
            return s.executeUpdate();
        });

    }
    public <T> T run(InsertWithGeneratedKeys<T> ik){
        InsertSqlBuilder b = new InsertSqlBuilder(dbType,ik.getInsert(),ik.getGenerated());
        String sql = b.generate();
        log.debug(sql);
        return trans.run(c -> {
            PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int count = s.executeUpdate();
            ExprRowReader exprReader = new ExprRowReader();


            try (ResultSet generatedKeys = s.getGeneratedKeys()) {
                ResultSetRowReader rowReader = new ResultSetRowReader(generatedKeys);
                if (generatedKeys.next()) {
                    return exprReader.read(ik.getGenerated(),rowReader);
                }
                throw new RuntimeException("No generated keys...");
            }
        });
    }

    public <T> PList<T> run(Selection<T> selection){
        QuerySqlBuilder b = new QuerySqlBuilder(selection,dbType);
        String sql = b.generate();
        log.debug(sql);
        return trans.run(c-> {
            PreparedStatement s = c.prepareStatement(sql);
            ExprRowReader exprReader = new ExprRowReader();
            try(ResultSet rs = s.executeQuery()){
                ResultSetRowReader rowReader = new ResultSetRowReader(rs);
                PList<T> res = PList.empty();
                while(rs.next()){
                    res = res.plus(exprReader.read(selection.getSelection(),rowReader));
                    rowReader.nextRow();
                }
                return res;
            }
        });
    }

}