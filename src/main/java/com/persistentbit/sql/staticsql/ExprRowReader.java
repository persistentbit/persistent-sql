package com.persistentbit.sql.staticsql;

import com.persistentbit.core.collections.PList;
import com.persistentbit.core.tuples.*;
import com.persistentbit.core.utils.NotYet;
import com.persistentbit.sql.staticsql.expr.*;

import java.lang.reflect.Method;

/**
 * Created by petermuys on 3/10/16.
 */
public class ExprRowReader {



    public <T> T read(Expr<T> expr, RowReader reader){
        return (T) new Visitor(reader).visit(expr);
    }

    private class Visitor implements ExprVisitor<Object>{
        private RowReader reader;

        public Visitor(RowReader reader){
            this.reader = reader;
        }
        @Override
        public Object visit(EMapper mapper) {
            return mapper.getMapper().apply(visit(mapper.getExpr()));
        }

        private Object error(Expr v) {
            throw new RuntimeException("Can't read from row for expression " + v);
        }
        @Override
        public Object visit(ETypeObject v) {
            try{
                Class cls = v.getClass();
                Method m = cls.getDeclaredMethod("read",RowReader.class,boolean.class);
                return m.invoke(null,reader,true);
            }catch (Exception e){
                throw new RuntimeException("Error reading for expr "+ v,e);
            }
        }

        @Override
        public Object visit(ExprProperty v) {
            switch(v.getValueClass().getSimpleName()){
                case "Integer": return reader.readNext(Integer.class);
                case "Long": return reader.readNext(Long.class);
                case "Short": return reader.readNext(Short.class);
                case "Byte": return reader.readNext(Byte.class);
                case "Float": return reader.readNext(Float.class);
                case "Double": return reader.readNext(Double.class);
                case "Boolean": return reader.readNext(Boolean.class);
                case "String": return reader.readNext(String.class);
            }
            //Must be a case class...
            return error(v);
            //return reader.readNext(v.getValueClass());
        }

        @Override
        public Object visit(ExprAndOr v) {
            return reader.readNext(Boolean.class);
        }



        @Override
        public Object visit(ExprNumberToString v) {
            return reader.readNext(String.class);
        }

        @Override
        public Object visit(ExprBoolean v) {
            return reader.readNext(Boolean.class);
        }

        @Override
        public Object visit(ETuple2 v) {
            return new Tuple2(visit(v.getV1()),visit(v.getV2()));
        }

        @Override
        public Object visit(ETuple3 v) {
            return new Tuple3(visit(v.getV1()),visit(v.getV2()),visit(v.getV3()));
        }
        @Override
        public Object visit(ETuple4 v) {
            return new Tuple4(
                    visit(v.getV1()),
                    visit(v.getV2()),
                    visit(v.getV3()),
                    visit(v.getV4())
            );
        }
        @Override
        public Object visit(ETuple5 v) {
            return new Tuple5(
                    visit(v.getV1()),
                    visit(v.getV2()),
                    visit(v.getV3()),
                    visit(v.getV4()),
                    visit(v.getV5())
            );
        }
        @Override
        public Object visit(ETuple6 v) {
            return new Tuple6(
                    visit(v.getV1()),
                    visit(v.getV2()),
                    visit(v.getV3()),
                    visit(v.getV4()),
                    visit(v.getV5()),
                    visit(v.getV6())
            );
        }
        @Override
        public Object visit(ETuple7 v) {
            return new Tuple7(
                    visit(v.getV1()),
                    visit(v.getV2()),
                    visit(v.getV3()),
                    visit(v.getV4()),
                    visit(v.getV5()),
                    visit(v.getV6()),
                    visit(v.getV7())
            );
        }
        @Override
        public Object visit(ExprConstNumber v) {
            return reader.readNext(v.getValue().getClass());
        }

        @Override
        public Object visit(ExprNumberCast v) {
            return reader.readNext(v.getClsTo());
        }


        @Override
        public Object visit(ExprCompare v) {
            return reader.readNext(Boolean.class);
        }

        @Override
        public Object visit(ExprStringAdd v) {
            return reader.readNext(String.class);
        }

        @Override
        public Object visit(ExprConstString v) {
            return reader.readNext(String.class);
        }

        @Override
        public Object visit(ExprNumberBinOp v) {
            throw new NotYet("Need to find the right type...");
        }

        @Override
        public Object visit(EValTable v) {
            return v.getValue();
        }

    }
}
