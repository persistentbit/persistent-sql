package com.persistentbit.sql.staticsql;

import com.persistentbit.core.tuples.*;
import com.persistentbit.core.utils.NotYet;
import com.persistentbit.sql.staticsql.expr.*;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by petermuys on 3/10/16.
 */
public class ExprRowReader implements ExprRowReaderCache{

    private final Map<Object,Object> cache   =   new HashMap<>();
    private final Set<Object> usedFromCache = new HashSet<>();

    @Override
    public Object updatedFromCache(Object value){
        if(value == null){
            return null;
        }
        Object cached = cache.get(value);
        if(cached != null){
            //System.out.println("Cached: " + cached);
            usedFromCache.add(cached);
            return cached;
        }
        cache.put(value,value);
        return value;
    }


    public <T> T read(Expr<T> expr, RowReader reader){
        //return (T) new Visitor(reader).visitExpr(expr);
        return expr.read(reader,this);
    }
//
//    private class Visitor implements ExprVisitor<Object>{
//        private RowReader reader;
//
//        public Visitor(RowReader reader){
//            this.reader = reader;
//        }
//
//
//        private Object visitExpr(Expr e){
//            return e.accept(this);
//        }
//
//        @Override
//        public Object visit(EGroup group) {
//            return visitExpr(group.getValue());
//        }
//
//        @Override
//        public Object visit(ExprPropertyDate v) {
//            throw new NotYet();
//        }
//
//        @Override
//        public Object visit(ExprPropertyDateTime v) {
//            throw new NotYet();
//        }
//
//        @Override
//        public Object visit(EMapper mapper) {
//            Object value = visitExpr(mapper.getExpr());
//            if(usedFromCache.contains(value) == false){
//                cache.remove(value);
//            }
//            value = updatedFromCache(value);
//            return mapper.getMapper().apply(value);
//        }
//        private Map<Class,Method> readMethods = new HashMap<>();
//        private Method getTableReadMethod(ETypeObject v){
//            try{
//                Class cls = v.getClass();
//                Method m = readMethods.get(cls);
//                if(m != null){
//                    return m;
//                }
//                m = cls.getDeclaredMethod("read",RowReader.class,ExprRowReaderCache.class);
//                readMethods.put(cls,m);
//                return m;
//
//            }catch (Exception e){
//                throw new RuntimeException("Error reading for expr "+ v,e);
//            }
//        }
//
//
//        @Override
//        public Object visit(ETypeObject v) {
//            try{
//                return getTableReadMethod(v).invoke(null,reader,ExprRowReader.this);
//            }catch (Exception e){
//                throw new RuntimeException("Error reading for expr "+ v,e);
//            }
//        }
//
//        @Override
//        public Object visit(ETypeSelection v) {
//            return v.read(reader,this);
//        }
//
//        private Object error(Expr v) {
//            throw new RuntimeException("Can't read from row for expression " + v);
//        }
//
//        @Override
//        public Object visit(ExprProperty v) {
//            switch(v.getValueClass().getSimpleName()){
//                case "Integer": return reader.readNext(Integer.class);
//                case "Long": return reader.readNext(Long.class);
//                case "Short": return reader.readNext(Short.class);
//                case "Byte": return reader.readNext(Byte.class);
//                case "Float": return reader.readNext(Float.class);
//                case "Double": return reader.readNext(Double.class);
//                case "Boolean": return reader.readNext(Boolean.class);
//                case "String": return reader.readNext(String.class);
//            }
//            //Must be a case class...
//            return error(v);
//            //return reader.readNext(v.getValueClass());
//        }
//
//        @Override
//        public Object visit(ExprAndOr v) {
//            return reader.readNext(Boolean.class);
//        }
//
//
//        @Override
//        public Object visit(ExprEnum v) {
//            throw new NotYet();
//        }
//
//        @Override
//        public Object visit(ExprNumberToString v) {
//            return reader.readNext(String.class);
//        }
//
//        @Override
//        public Object visit(ExprBoolean v) {
//            return reader.readNext(Boolean.class);
//        }
//
//        @Override
//        public Object visit(ExprDate v) {
//            return reader.readNext(LocalDate.class);
//        }
//
//        @Override
//        public Object visit(ExprDateTime v) {
//            return reader.readNext(LocalDateTime.class);
//        }
//
//        @Override
//        public Object visit(ETuple2 v) {
//            return updatedFromCache(new Tuple2(visitExpr(v.getV1()),visitExpr(v.getV2())));
//        }
//
//        @Override
//        public Object visit(ETuple3 v) {
//            return new Tuple3(visitExpr(v.getV1()),visitExpr(v.getV2()),visitExpr(v.getV3()));
//        }
//        @Override
//        public Object visit(ETuple4 v) {
//            return updatedFromCache(new Tuple4(
//                    visitExpr(v.getV1()),
//                    visitExpr(v.getV2()),
//                    visitExpr(v.getV3()),
//                    visitExpr(v.getV4())
//            ));
//        }
//        @Override
//        public Object visit(ETuple5 v) {
//            return updatedFromCache(new Tuple5(
//                    visitExpr(v.getV1()),
//                    visitExpr(v.getV2()),
//                    visitExpr(v.getV3()),
//                    visitExpr(v.getV4()),
//                    visitExpr(v.getV5())
//            ));
//        }
//        @Override
//        public Object visit(ETuple6 v) {
//            return updatedFromCache(new Tuple6(
//                    visitExpr(v.getV1()),
//                    visitExpr(v.getV2()),
//                    visitExpr(v.getV3()),
//                    visitExpr(v.getV4()),
//                    visitExpr(v.getV5()),
//                    visitExpr(v.getV6())
//            ));
//        }
//        @Override
//        public Object visit(ETuple7 v) {
//            return updatedFromCache(new Tuple7(
//                    visitExpr(v.getV1()),
//                    visitExpr(v.getV2()),
//                    visitExpr(v.getV3()),
//                    visitExpr(v.getV4()),
//                    visitExpr(v.getV5()),
//                    visitExpr(v.getV6()),
//                    visitExpr(v.getV7())
//            ));
//        }
//
//
//
//        @Override
//        public Object visit(ExprConstNumber v) {
//            return reader.readNext(v.getValue().getClass());
//        }
//
//        @Override
//        public Object visit(ExprNumberCast v) {
//            return reader.readNext(v.getClsTo());
//        }
//
//
//        @Override
//        public Object visit(ExprCompare v) {
//            return reader.readNext(Boolean.class);
//        }
//
//        @Override
//        public Object visit(ExprStringAdd v) {
//            return reader.readNext(String.class);
//        }
//        @Override
//        public Object visit(ExprStringLike v) {
//            return reader.readNext(Boolean.class);
//        }
//
//        @Override
//        public Object visit(ExprConstString v) {
//            return reader.readNext(String.class);
//        }
//
//        @Override
//        public Object visit(ExprNumberBinOp v) {
//            throw new NotYet("Need to find the right type...");
//        }
//
//        @Override
//        public Object visit(EValTable v) {
//            return v.getValue();
//        }
//
//    }
}
