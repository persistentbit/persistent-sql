package com.persistentbit.sql.statement;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.collections.PMap;
import com.persistentbit.core.function.NamedSupplier;
import com.persistentbit.sql.PersistSqlException;
import com.persistentbit.sql.dbdef.TableDef;
import com.persistentbit.sql.objectmappers.ReadableRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * User: petermuys
 * Date: 16/07/16
 * Time: 16:00
 */
public class EStatementPreparer {
    static private final Logger log = Logger.getLogger(EStatementPreparer.class.getName());
    private final Function<String,TableDef> tableDefSupplier;


    public EStatementPreparer(Function<String, TableDef> tableDefSupplier) {
        this.tableDefSupplier = tableDefSupplier;
    }
    public PreparedStatement prepare(Connection c, String sql, ReadableRow args){
        return prepare(c,sql,args,false);
    }

    public PreparedStatement prepare(Connection c, String sql, ReadableRow args, boolean autGenKeys){
        PList<ESqlParser.Token> sqlTokens = ESqlParser.parse(sql);
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

        try {
            PreparedStatement s = autGenKeys ? c.prepareStatement(js, Statement.RETURN_GENERATED_KEYS)  : c.prepareStatement(js);
            argNames.zipWithIndex().forEach(n -> {
                try {
                    s.setObject(n._1+1,args.read(n._2));
                } catch (SQLException e) {
                    throw new PersistSqlException(e);
                }
            });
            return s;
        } catch (SQLException e) {
            log.severe("Error preparing statement " + js);
            throw new PersistSqlException(e);
        }


    }
}
