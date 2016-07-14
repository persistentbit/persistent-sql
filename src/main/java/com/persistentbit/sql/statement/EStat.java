package com.persistentbit.sql.statement;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.connect.SQLRunner;
import com.persistentbit.sql.dbdef.DbDef;
import com.persistentbit.sql.dbdef.TableDef;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 14/07/2016
 */
public class EStat implements SqlArguments<EStat>{
    private final DbDef dbDef;
    private final SQLRunner runner;
    private String sql;
    private PList<ESqlParser.Token> sqlTokens;
    private PMap<String,Object> args = PMap.empty();

    public EStat(DbDef dbDef,SQLRunner runner){
        this.dbDef = dbDef;
        this.runner = runner;
    }
    public EStat sql(String...sql){

        this.sql = PStream.from(sql).toString(" ");
        this.sqlTokens = ESqlParser.parse(this.sql);
        return this;
    }

    public PList<Record>  select(){
        return select(s  ->s.plist());
    }


    public void selectAndDo(Consumer<PStream<Record>> code){
        runner.run(c -> {
            try(PreparedStatement stat = prepare(c)){
                code.accept(new ResultSetRecordStream(stat.executeQuery()));
            }
        });
    }

    public <T> PList<T> selectMap(Function<Record,T> mapper){
        return select(f->f.map(mapper).plist());
    }

    public <T> T  select(Function<PStream<Record>,T> code){
        return runner.run(c -> {
            try(PreparedStatement stat = prepare(c)){
                return code.apply(new ResultSetRecordStream(stat.executeQuery()));
            }
        });
    }

    private PreparedStatement prepare(Connection c){
        PMap<String,TableDef> aliases =
                sqlTokens.filter( f-> f instanceof ESqlParser.TableAsToken)
                .map(f -> (ESqlParser.TableAsToken)f)
                .groupBy(f-> f.alias)
                .mapValues(v -> dbDef.get(v.head().tableName));
        PList<String> argNames =
                sqlTokens.filter( f-> f instanceof ESqlParser.ArgToken)
                .map(f -> ((ESqlParser.ArgToken)f).fieldName);
        String js = sqlTokens.map(t->{
            if(t instanceof ESqlParser.TableAsToken){
                ESqlParser.TableAsToken tas = (ESqlParser.TableAsToken)t;
                return tas.tableName + " AS " + tas.alias;
            } else if(t instanceof ESqlParser.ArgToken){
                return "?";
            } else if(t instanceof ESqlParser.StringToken){
                return t.toString();
            } else if(t instanceof ESqlParser.TableFieldsToken){
                ESqlParser.TableFieldsToken tf = (ESqlParser.TableFieldsToken)t;
                String al = tf.tableName;
                TableDef td = aliases.get(al);
                return td.getCols().map(col -> al + "." + col.getName() + " as " + al + "_" + col.getName()).toString(",");
            }else {
                throw new RuntimeException(t.toString());
            }
        }).join((a,b)->a+b).get();
        System.out.println(js);
        try {
            PreparedStatement s = c.prepareStatement(js);
            argNames.zipWithIndex().forEach(n -> {
                try {
                    s.setObject(n._1+1,args.get(n._2));
                } catch (SQLException e) {
                    throw new PersistSqlException(e);
                }
            });
            return s;
        } catch (SQLException e) {
            throw new PersistSqlException(e);
        }


    }

    @Override
    public EStat arg(String name, Object value) {
        args = args.put(name,value);
        return this;
    }

    @Override
    public String toString() {
        return sqlTokens.toString();
    }
}
