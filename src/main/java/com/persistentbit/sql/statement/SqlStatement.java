package com.persistentbit.sql.statement;


import com.persistentbit.core.Pair;


import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author Peter Muys
 * @since 29/02/2016
 */
public class SqlStatement implements SqlArguments<SqlStatement>{
    private final Supplier<Connection> connectionSupplier;
    private String sql;
    private Map<String,Object> values = new LinkedHashMap<>();

    public SqlStatement(Supplier<Connection> connectionSupplier){
        this.connectionSupplier = connectionSupplier;
    }
    public SqlStatement sql(String sql){
        this.sql = sql;
        return this;
    }
    public SqlStatement arg(String name, Object value){
        values.put(name,value);
        return this;
    }

    public String getSql() {
        return sql;
    }


    private void setStatementParams(Pair<String, List<String>> transformed, PreparedStatement stat) throws SQLException {
        for(int t=0; t<transformed.getRight().size();t++){
            String name = transformed.getRight().get(t);
            if(values.containsKey(name) == false){
                throw new RuntimeException("Unknown name : \"" + name + "\"");
            }
            stat.setObject(t+1,values.get(name));
        }
    }

    private Pair<String,List<String>> buildJdbcSql(){
        String result = "";
        boolean inString = false;
        String varName = null;
        List<String> names = new ArrayList<>();
        for(int t=0; t<sql.length(); t++){
            char c = sql.charAt(t);
            if(varName != null && (Character.isJavaIdentifierStart(c) || Character.isJavaIdentifierPart(c)) || c == '(' || c == ')' || c=='.' || c=='*'){
                varName += c;
                continue;
            }
            if (varName != null) {
                result += "?";


                names.add(varName);
                varName = null;

            }
            if(c == '\''){
                if(inString == false){
                    result += "\'";
                    inString = true;
                } else {
                    if(t != sql.length()-1) {
                        if (sql.charAt(t + 1) == '\'') {
                            //we have a double quote
                            result += "''";
                            t += 1;
                            continue;
                        }
                    }
                    result += "\'";
                    inString = false;
                }
            } else if(c == ':' && inString == false) {
                varName = "";
                //result += "?";
            } else {
                result += c;
            }
        }
        if(inString){
            throw new RuntimeException("Unterminated string in " + sql);
        }
        if (varName != null) {
            names.add(varName);
            varName = null;
        }
        return new Pair<>(result,names);
    }

    static public void main(String...args){
        SqlStatement stat = new SqlStatement(null);
        stat.sql("select :inv.* from :invoice.as(inv)\n" +
                "left join :invoice_line.as(line) on line.invoice_id = inv.id\n" +
                "where inv.id=:id");
        System.out.println(stat.buildJdbcSql());
    }

}
