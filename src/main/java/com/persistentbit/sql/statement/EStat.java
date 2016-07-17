package com.persistentbit.sql.statement;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.collections.PStream;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.connect.SQLRunner;
import com.persistentbit.sql.dbdef.TableDef;

import java.sql.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Peter Muys
 * @since 14/07/2016
 */
public class EStat implements SqlArguments<EStat>{
    private final Function<String,TableDef> tableDefSupplier;
    private final SQLRunner runner;
    private String sql;
    private PList<ESqlParser.Token> sqlTokens;
    private PMap<String,Object> args = PMap.empty();


    public EStat(SQLRunner runner){
        this(runner,n -> {
            throw new PersistSqlException("Don't know how to supply a TableDef for the table named '" + n + "'");
        });
    }


    public EStat(SQLRunner runner,Function<String,TableDef> tableDefSupplier){
        this.tableDefSupplier = tableDefSupplier;
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


    public void selectAndForEach(Consumer<PStream<Record>> code){
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

    public boolean execute() {
        return runner.run(c -> {
            try(PreparedStatement s = prepare(c)){
                return s.execute();
            }
        });
    }

    public int update() {
        return runner.run(c-> {
           try(PreparedStatement s = prepare(c)){
               return s.executeUpdate();
           }
        });
    }

    public void updateOne() {
        int count = update();
        if(count != 1){
            throw new PersistSqlException("Expected 1 update. Got " + count + " updates instead." );
        }
    }

    private PreparedStatement prepare(Connection c){
        PMap<String,TableDef> aliases =
                sqlTokens.filter( f-> f instanceof ESqlParser.TableAsToken)
                .map(f -> (ESqlParser.TableAsToken)f)
                .groupBy(f-> f.alias)
                .mapValues(v -> tableDefSupplier.apply(v.head().tableName));
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
        }).join(
                (a,b)->a+b).get();
        //System.out.println(js);
        try{
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
            throw new PersistSqlException("Error preparing statement \"" + js+ "\"",e);
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


    public boolean tableExists(String tableName){
        return runner.run(c -> {
            DatabaseMetaData dbm = c.getMetaData();
            try(ResultSet rs = dbm.getTables(null,null,null,null)){
                PStream<Record>  recStream = new ResultSetRecordStream(rs);
                PStream<String> resStream = recStream.map(r -> r.getString("table_name"));
                PList<String> res = resStream.plist();
                return res.find(r -> r.equalsIgnoreCase(tableName)).isPresent();
            }
        });

    }
}
