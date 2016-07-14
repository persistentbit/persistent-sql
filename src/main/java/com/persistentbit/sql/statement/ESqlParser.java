package com.persistentbit.sql.statement;

import com.persistentbit.core.collections.PList;

/**
 * @author Peter Muys
 * @since 14/07/2016
 */
public class ESqlParser {
    public interface Token{

    }
    static public class StringToken implements Token{
        public final String txt;

        public StringToken(String txt) {
            this.txt = txt;
        }

        @Override
        public String toString() {
            return txt;
        }
    }
    static public class TableFieldsToken implements  Token{
        public String tableName;

        public TableFieldsToken(String tableName) {
            this.tableName = tableName;
        }

        @Override
        public String toString() {
            return "[" + tableName + ".*]";
        }
    }
    static public class TableAsToken implements  Token{
        public final String tableName;
        public final String alias;

        public TableAsToken(String tableName, String alias) {
            this.tableName = tableName;
            this.alias = alias;
        }

        @Override
        public String toString() {
            return "[" + tableName + " as " + alias + "]";
        }
    }
    static public class ArgToken implements  Token{
        public final String fieldName;

        public ArgToken(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public String toString() {
            return ":" + fieldName;
        }
    }
    private PList<Token> tokens = PList.empty();
    private int pos = -1;
    private char c;
    private boolean eof = false;
    private String s;
    private ESqlParser(String esql){
        s = esql;
        next();
        while(!eof){
            parseToken();
        }
    }

    private char next() {
        if(eof){
            throw new IllegalStateException("Unexpected end");
        }
        if(pos >= s.length()-1){
            eof =true;
            c = 0;
        } else {
            pos ++;
            c = s.charAt(pos);
        }
        return c;
    }

    private void parseToken() {
        if(c == ':'){
            parseSpecial();
        } else if(c == '\'' || c=='\"'){
            parseString();
        } else {
            parseNormal();
        }
    }
    private void parseNormal(){
        String res = "";
        while(eof==false && c != '\'' && c != '\"' && c != ':') {
            res += c;
            next();
        }
        tokens = tokens.plus(new StringToken(res));
    }
    private void parseString() {
        String res = "" + c;
        char start = c;
        next();
        do{
            if(eof){
                throw new IllegalStateException("String nog closed: " + res);
            }
            if(c == start){
                res += c;
                next();
                if(c == start){
                    res += c;
                    next();
                }else {
                    break;
                }
            } else {
                res += c;
                next();
            }
        }while(true);
        tokens = tokens.plus(new StringToken(res));
    }
    private void parseSpecial() {
        String first = "";
        next();
        do{

            if(c == '.' || eof || Character.isJavaIdentifierPart(c)==false){
                break;
            }
            first = first + c;
            next();
        }while(true);
        if(c != '.'){
            tokens = tokens.plus(new ArgToken(first));
            return;
        }
        next(); //skip .
        if(c == '*'){
            next(); //skip *
            tokens = tokens.plus(new TableFieldsToken(first));
            return;
        }
        String n = "" +c; //a
        next();
        n += c; //s
        next();
        n += c; //.
        next();
        if(n.equalsIgnoreCase("as.") == false){
            throw new IllegalStateException("Expected :" + first + ".as.alias ");
        }
        String second  = "";
        do{
            second += c;
            next();
            if(eof || Character.isJavaIdentifierPart(c)==false){
                break;
            }
        }while(true);
        tokens = tokens.plus(new TableAsToken(first,second));
    }

    static public PList<Token> parse(String esql){
        return new ESqlParser(esql).tokens;
    }
    static public void main(String...args){
        String esql = "select :p.* from :persoon.as.p where p.rrn=:rrn and name not like ':''test'";
        PList<Token> tokens = parse(esql);
        System.out.println(tokens);
    }
}
